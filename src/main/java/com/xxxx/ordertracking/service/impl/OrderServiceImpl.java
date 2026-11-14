package com.xxxx.ordertracking.service.impl;

import com.xxxx.ordertracking.common.exception.ApiException;
import com.xxxx.ordertracking.common.exception.ErrorCode;
import com.xxxx.ordertracking.dto.request.CreateOrderRequest;
import com.xxxx.ordertracking.dto.response.CreateOrderResponse;
import com.xxxx.ordertracking.entity.*;
import com.xxxx.ordertracking.repository.*;
import com.xxxx.ordertracking.service.OrderService;
import com.xxxx.ordertracking.service.CheckoutPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartRepository cartRepository;
    private final CarrierRepository carrierRepository;
    private final CheckoutPricingService checkoutPricingService;

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

        var quote = checkoutPricingService.calculate(accountId, request.addressId(), request.carrierId());
        var summary = quote.summary();
        if (!summary.canPlaceOrder()) {
            boolean unavailable = summary.issues().stream()
                    .anyMatch(issue -> ErrorCode.PRODUCT_NOT_AVAILABLE.name().equals(issue.reason()));
            throw new ApiException(unavailable ? ErrorCode.PRODUCT_NOT_AVAILABLE : ErrorCode.INSUFFICIENT_STOCK);
        }

        var cartItems = quote.items().stream()
                .sorted(Comparator.comparing(item -> item.getProductVariant().getId()))
                .toList();
        for (CartItem item : cartItems) {
            if (productVariantRepository.reserveStock(item.getProductVariant().getId(), item.getQuantity()) != 1) {
                throw new ApiException(ErrorCode.INSUFFICIENT_STOCK);
            }
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
}
