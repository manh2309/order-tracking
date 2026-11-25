package com.xxxx.ordertracking.service.impl;

import com.xxxx.ordertracking.common.exception.ApiException;
import com.xxxx.ordertracking.common.exception.ErrorCode;
import com.xxxx.ordertracking.dto.request.CreateOrderRequest;
import com.xxxx.ordertracking.dto.request.CreateShipmentRequest;
import com.xxxx.ordertracking.dto.response.CreateOrderResponse;
import com.xxxx.ordertracking.dto.response.CreateShipmentResponse;
import com.xxxx.ordertracking.dto.response.OrderSummary;
import com.xxxx.ordertracking.dto.response.OrderDetailResponse;
import com.xxxx.ordertracking.entity.*;
import com.xxxx.ordertracking.enums.OrderStatus;
import com.xxxx.ordertracking.repository.*;
import com.xxxx.ordertracking.service.OrderService;
import com.xxxx.ordertracking.service.CheckoutPricingService;
import com.xxxx.ordertracking.service.StockReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductVariantRepository productVariantRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CarrierRepository carrierRepository;
    private final CheckoutPricingService checkoutPricingService;
    private final StockReservation stockReservation;
    private final CartItemRepository cartItemRepository;
    private final AccountRepository accountRepository;
    private final ShipmentRepository shipmentRepository;
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CreateOrderResponse createOrder(CreateOrderRequest request, String accountId) {
        if (!"COD".equals(request.paymentMethod())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "UNSUPPORTED_PAYMENT_METHOD",
                    "Hiện chỉ hỗ trợ thanh toán COD");
        }
        String fingerprint = fingerprint(request);
        var existing = orderRepository.findByAccount_IdAndCheckoutRequestId(accountId, request.checkoutRequestId());
        if (existing.isPresent()) {
            return replay(existing.get(), fingerprint);
        }

        Cart cart = cartRepository.lockActiveCart(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.CART_EMPTY));

        existing = orderRepository.findByAccount_IdAndCheckoutRequestId(accountId, request.checkoutRequestId());
        if (existing.isPresent()) {
            return replay(existing.get(), fingerprint);
        }
        var cartItems = cartItemRepository.findActiveItemsByAccountId(accountId).stream()
                .sorted(Comparator.comparing(item -> item.getProductVariant().getId()))
                .toList();
        if (cartItems.isEmpty()) {
            throw new ApiException(ErrorCode.CART_EMPTY);
        }

        stockReservation.prepare(cartItems);
        var quote = checkoutPricingService.calculate(
                accountId, request.addressId(), request.carrierId(), cartItems);
        var summary = quote.summary();
        if (!summary.canPlaceOrder()) {
            boolean unavailable = summary.issues().stream()
                    .anyMatch(issue -> ErrorCode.PRODUCT_NOT_AVAILABLE.name().equals(issue.reason()));
            throw new ApiException(unavailable ? ErrorCode.PRODUCT_NOT_AVAILABLE : ErrorCode.INSUFFICIENT_STOCK);
        }

        for (CartItem item : cartItems) {
            stockReservation.reserve(item.getProductVariant(), item.getQuantity());
        }

        var address = quote.address();
        Order order = new Order();
        order.setCode("DH-" + UUID.randomUUID());
        order.setCheckoutRequestId(request.checkoutRequestId());
        order.setCheckoutRequestFingerprint(fingerprint);
        order.setAccount(cart.getAccount());
        order.setCarrier(carrierRepository.getReferenceById(request.carrierId()));
        order.setPaymentMethod(request.paymentMethod());
        order.setCurrency(summary.currency());
        order.setSubtotal(summary.subtotal());
        order.setDiscountAmount(summary.discountAmount());
        order.setShippingFee(summary.shippingFee());
        order.setGrandTotal(summary.grandTotal());
        order.setRecipientName(address.getRecipientName());
        order.setRecipientPhone(address.getRecipientPhone());
        order.setProvinceCode(address.getProvinceCode());
        order.setWardCode(address.getWardCode());
        order.setAddressLine(address.getAddressLine());

        Order saved = orderRepository.save(order);
        orderItemRepository.saveAll(cartItems.stream().map(item -> toOrderItem(saved, item)).toList());
        cartItems.forEach(item -> item.setDeleted((byte) 1));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public OrderSummary confirm(String orderId) {
        int updated = orderRepository.confirmOrder(orderId, OrderStatus.PENDING.getCode(),
                OrderStatus.CONFIRMED.getCode(), LocalDateTime.now(ZoneOffset.UTC));
        OrderSummary result = orderRepository.activeSummary(orderId)
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));
        if (updated == 0) {
            throw new ApiException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse detail(String accountId, String id) {
        Order order = orderRepository.findByIdAndAccount_IdAndDeleted(id, accountId, (byte) 0)
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));
        return new OrderDetailResponse(summary(order), order.getRecipientName(), order.getRecipientPhone(),
                order.getAddressLine(), order.getWardCode(), order.getProvinceCode(), order.getPaymentMethod(),
                order.getCancelReason(), orderItemRepository.orderLines(id));
    }

    @Override
    @Transactional
    public OrderSummary cancel(String accountId, String id, String reason) {
        Order order = orderRepository.lockCustomerOrder(accountId, id)
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));
        if (order.getStatus() == OrderStatus.CANCELLED) return summary(order);
        if (order.getStatus() != OrderStatus.PENDING || !"COD".equals(order.getPaymentMethod())) {
            throw new ApiException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
        for (var item : orderItemRepository.findByOrder_IdAndDeletedOrderByProductVariant_IdAsc(id, (byte) 0)) {
            if (productVariantRepository.restoreStock(item.getProductVariant().getId(), item.getQuantity()) != 1) {
                throw new ApiException(ErrorCode.STOCK_UPDATE_CONFLICT);
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now(ZoneOffset.UTC));
        order.setCancelReason(reason.strip());
        return summary(order);
    }

    @Override
    @Transactional
    public CreateShipmentResponse createShipment(String orderId, CreateShipmentRequest request) {
        Order order = orderRepository.lockActiveOrder(orderId)
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new ApiException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
        if (shipmentRepository.existsByOrderId(orderId)) {
            throw new ApiException(ErrorCode.SHIPMENT_EXISTED);
        }
        Account shipper = accountRepository.findActiveShipper(request.shipperId())
                .orElseThrow(() -> new ApiException(ErrorCode.SHIPPER_UNAVAILABLE));
        if (order.getCarrier() == null) {
            throw new ApiException(ErrorCode.CARRIER_NOT_AVAILABLE);
        }
        Long weight = orderItemRepository.totalWeightGrams(orderId);
        if (weight == null || weight < 0 || weight > Integer.MAX_VALUE) {
            throw new ApiException(ErrorCode.INVALID_SHIPMENT_WEIGHT);
        }

        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setShipper(shipper);
        shipment.setCarrier(order.getCarrier());
        shipment.setTotalWeightGrams(weight.intValue());
        Shipment saved = shipmentRepository.save(shipment);
        return new CreateShipmentResponse(saved.getId(), orderId, saved.getStatus());
    }

    private static OrderItem toOrderItem(Order order, CartItem cartItem) {
        ProductVariant variant = cartItem.getProductVariant();
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductVariant(variant);
        item.setSkuSnapshot(variant.getSku());
        item.setProductNameSnapshot(variant.getProduct().getName());
        String description = Stream.of(
                        variant.getColor() == null ? null : variant.getColor().getName(),
                        variant.getSize() == null ? null : variant.getSize().getName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" / "));
        item.setVariantDescriptionSnapshot(description.isEmpty() ? null : description);
        item.setUnitPrice(variant.getPrice());
        item.setQuantity(cartItem.getQuantity());
        item.setDiscountAmount(BigDecimal.ZERO);
        item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return item;
    }

    private static CreateOrderResponse replay(Order order, String fingerprint) {
        if (!fingerprint.equals(order.getCheckoutRequestFingerprint())) {
            throw new ApiException(ErrorCode.CHECKOUT_REQUEST_CONFLICT);
        }
        return toResponse(order);
    }

    private static CreateOrderResponse toResponse(Order order) {
        return new CreateOrderResponse(order.getId(), order.getCode(),
                order.getStatus().name(), order.getGrandTotal(), order.getCurrency());
    }

    private static String fingerprint(CreateOrderRequest request) {
        String input = Stream.of(request.addressId(), request.carrierId(), request.paymentMethod())
                .map(value -> value.length() + ":" + value)
                .collect(Collectors.joining());
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }

    private static OrderSummary summary(Order o) {
        return new OrderSummary(o.getId(), o.getCode(), o.getCreatedAt(), o.getStatus(), o.getSubtotal(),
                o.getDiscountAmount(), o.getShippingFee(), o.getGrandTotal(), o.getCurrency());
    }
}
