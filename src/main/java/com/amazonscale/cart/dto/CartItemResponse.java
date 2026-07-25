package com.amazonscale.cart.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productDescription;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private String imageUrl;
}
