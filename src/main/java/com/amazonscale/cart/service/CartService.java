package com.amazonscale.cart.service;

import com.amazonscale.cart.dto.AddToCartRequest;
import com.amazonscale.cart.dto.CartResponse;
import com.amazonscale.cart.dto.UpdateCartItemRequest;

public interface CartService {

    CartResponse addItemToCart(Long userId ,AddToCartRequest request);
    CartResponse updateCartItem(Long userId,Long productId,UpdateCartItemRequest request);
    void removeCartItem(Long userId,Long productId);
    void clearCart(Long userId);
    CartResponse getCart(Long userId);

}
