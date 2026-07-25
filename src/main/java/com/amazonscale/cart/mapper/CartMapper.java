package com.amazonscale.cart.mapper;

import com.amazonscale.cart.dto.CartItemResponse;
import com.amazonscale.cart.dto.CartResponse;
import com.amazonscale.cart.entity.Cart;
import com.amazonscale.cart.entity.CartItem;
import com.amazonscale.cart.entity.CurrencyCode;

import java.math.BigDecimal;
import java.util.List;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartItemResponse toCartItemResponse(CartItem cartItem) {

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productDescription(cartItem.getProduct().getDescription())
                .unitPrice(cartItem.getPriceAtAddition())
                .quantity(cartItem.getQuantity())
                .subtotal(
                        cartItem.getPriceAtAddition()
                                .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                )
                .imageUrl(cartItem.getProduct().getImageUrl())
                .build();
    }

    public static CartResponse toCartResponse(Cart cart) {

        List<CartItemResponse> items = cart.getCartItems()
                .stream()
                .map(CartMapper::toCartItemResponse)
                .toList();

        int totalItems = cart.getCartItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalAmount = cart.getCartItems()
                .stream()
                .map(item -> item.getPriceAtAddition()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .updatedAt(cart.getUpdatedAt())
                .currency(CurrencyCode.INR)
                .build();
    }
}