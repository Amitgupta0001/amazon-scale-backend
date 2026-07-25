package com.amazonscale.cart.dto;

import com.amazonscale.cart.entity.CurrencyCode;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private LocalDateTime updatedAt;
    private CurrencyCode currency;
}
