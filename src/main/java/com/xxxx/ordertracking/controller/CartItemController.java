package com.xxxx.ordertracking.controller;

import com.xxxx.ordertracking.common.response.ApiResponse;
import com.xxxx.ordertracking.config.AccountPrincipal;
import com.xxxx.ordertracking.dto.response.CartItemResponse;
import com.xxxx.ordertracking.dto.request.UpdateCartItemRequest;
import com.xxxx.ordertracking.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart-item")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping()
    public ApiResponse<Page<CartItemResponse>> findCartItemByCart(Pageable pageable, @AuthenticationPrincipal AccountPrincipal principal) {
        return ApiResponse.ok(cartItemService.findCartItemByCart(principal.getAccountId(), pageable));
    }

    @PatchMapping("/detail/{itemId}")
    public ApiResponse<CartItemResponse> updateQuantity(@PathVariable("itemId") String cartItemId,
                                                        @Valid @RequestBody UpdateCartItemRequest request,
                                                        @AuthenticationPrincipal AccountPrincipal principal) {
        return ApiResponse.ok(cartItemService.updateQuantity(principal.getAccountId(), cartItemId, request));
    }

}
