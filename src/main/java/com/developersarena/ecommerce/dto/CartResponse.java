package com.developersarena.ecommerce.dto;

import com.developersarena.ecommerce.entity.Cart;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private BigDecimal totalPrice;
    private LocalDateTime updateAt;
    private List<CartItemResponse> items;
}

