package com.developersarena.ecommerce.repository;

import com.developersarena.ecommerce.entity.Order;
import com.developersarena.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
