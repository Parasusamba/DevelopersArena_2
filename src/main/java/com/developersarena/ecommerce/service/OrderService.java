package com.developersarena.ecommerce.service;

import com.developersarena.ecommerce.dto.OrderRequest;
import com.developersarena.ecommerce.dto.OrderResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(Long id);

    Page<OrderResponse> getUserOrder(Pageable pageable);

    OrderResponse updateOrderStatus(Long id, String status);

    Page<OrderResponse> getAllUsersOrders(Pageable pageable);

    OrderResponse cancelOrder(Long id);
}
