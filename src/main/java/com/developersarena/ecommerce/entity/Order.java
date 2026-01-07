package com.developersarena.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(precision =  10, scale = 2)
    private BigDecimal shippingCharge;

    @Column(precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(name = "final_amount", precision = 10, scale = 2)
    private BigDecimal finalAmount;

    private String shippingAddress;

    private String billingAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected  void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if(orderNumber == null) {
            orderNumber = "ORD-"+System.currentTimeMillis();
        }
        if(status == null) {
            status = OrderStatus.PENDING;
        }
    }

    public void calculateAmounts() {
        // total from items
        this.totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // default shipping charge
        if(shippingCharge == null) {
            shippingCharge = BigDecimal.valueOf(40); // ₹40 default
        }
        // tax ( 18% GST)
        if(tax == null) {
            tax = totalAmount.multiply(BigDecimal.valueOf(0.18));
        }

        // final amount
        this.finalAmount = totalAmount.add(shippingCharge).add(tax);
    }

    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
    }

    public enum PaymentMethod {
        COD, CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING
    }

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        CANCELLED,
        REFUNDED
    }

}
