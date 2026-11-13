package com.xxxx.ordertracking.service.impl;

import com.xxxx.ordertracking.common.exception.ApiException;
import com.xxxx.ordertracking.common.exception.ErrorCode;
import com.xxxx.ordertracking.dto.response.CartItemResponse;
import com.xxxx.ordertracking.dto.request.UpdateCartItemRequest;
import com.xxxx.ordertracking.entity.Cart;
import com.xxxx.ordertracking.entity.CartItem;
import com.xxxx.ordertracking.mapper.CartItemMapper;
import com.xxxx.ordertracking.repository.CartItemRepository;
import com.xxxx.ordertracking.repository.CartRepository;
import com.xxxx.ordertracking.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Override
    public Page<CartItemResponse> findCartItemByCart(String accountId, Pageable pageable) {

        Cart cart = cartRepository.findAllByAccountId(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.CART_NOT_FOUND));
        Page<CartItemResponse> cartItemResponses = cartItemRepository.findByCartId(cart.getId(), pageable).map(CartItemMapper::toResponse);

        return cartItemResponses;
    }

    @Override
    @Transactional
    public CartItemResponse updateQuantity(String accountId, String itemId, UpdateCartItemRequest updateCartItemRequest) {
        cartRepository.lockActiveCart(accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.CART_ITEM_NOT_FOUND));
        CartItem cartItem = cartItemRepository.findActiveItem(itemId, accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.CART_ITEM_NOT_FOUND));
        var variant = cartItem.getProductVariant();

        if (updateCartItemRequest.quantity() > cartItem.getQuantity()) {
            var product = variant.getProduct();
            if (variant.getDeleted() != 0 || variant.getStatus() != 1
                    || product.getDeleted() != 0 || product.getStatus() != 1) {
                throw new ApiException(ErrorCode.PRODUCT_NOT_AVAILABLE);
            }
            if (updateCartItemRequest.quantity() > variant.getQuantityInStock()) {
                throw new ApiException(ErrorCode.INSUFFICIENT_STOCK);
            }
        }

        cartItem.setQuantity(updateCartItemRequest.quantity());
        cartItemRepository.save(cartItem);
        return CartItemMapper.toResponse(cartItem);
    }
}
