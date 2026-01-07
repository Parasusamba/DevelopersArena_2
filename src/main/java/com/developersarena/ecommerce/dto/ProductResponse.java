package com.developersarena.ecommerce.dto;

import com.developersarena.ecommerce.repository.CategoryRepository;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private Integer quantity;
    private String brand;
    private String color;
    private String size;
    private String imageUrl;
    private CategoryResponse category;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
}
