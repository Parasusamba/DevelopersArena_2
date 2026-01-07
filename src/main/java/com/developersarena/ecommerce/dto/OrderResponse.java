package com.developersarena.ecommerce.dto;

import com.developersarena.ecommerce.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private BigDecimal shippingCharge;
    private BigDecimal tax;
    private BigDecimal finalAmount;
    private String shippingAddress;
    private String billingAddress;
    private String status;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}

