package com.xxxx.ordertracking.service;

import com.xxxx.ordertracking.dto.response.CartItemResponse;
import com.xxxx.ordertracking.dto.request.UpdateCartItemRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CartItemService {
    Page<CartItemResponse> findCartItemByCart(String accountId, Pageable pageable);

    CartItemResponse updateQuantity(String accountId, String itemId, UpdateCartItemRequest updateCartItemRequest);

}
