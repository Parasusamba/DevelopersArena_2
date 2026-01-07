package com.developersarena.ecommerce.service.impl;

import com.developersarena.ecommerce.dto.*;
import com.developersarena.ecommerce.entity.*;
import com.developersarena.ecommerce.exception.NotFoundException;
import com.developersarena.ecommerce.repository.CartRepository;
import com.developersarena.ecommerce.repository.OrderRepository;
import com.developersarena.ecommerce.repository.ProductRepository;
import com.developersarena.ecommerce.repository.UserRepository;
import com.developersarena.ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));

        Cart cart = cartRepository.findByUser(user).orElseThrow(
                () -> new NotFoundException("Cart not found ", "CART_NOT_FOUND")
        );

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        // create oder
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress() != null ?
                request.getBillingAddress() : request.getShippingAddress());
        order.setPaymentMethod(Order.PaymentMethod.valueOf(request.getPaymentMethod()));
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        // convert cart items into order items
        for(CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            // check stock
            if(product.getQuantity() < cartItem.getQuantity()) {
                throw  new RuntimeException("Product quantity less than requested quantity for product: " + product.getName());
            }
            //update product quantity
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            //create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getDiscountedPrice() != null ?
                    product.getDiscountedPrice() : product.getPrice());
            orderItem.calculatePrices();
            order.getItems().add(orderItem);
        }
        order.calculateAmounts();
        // clear cart
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
        //save order
        order = orderRepository.save(order);
        return convertToResponse(order);
    }
    @Override
    public OrderResponse getOrderById(Long id) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Order not found with id: " + id, "ORDER_NOT_FOUND"
        ));
        if(!order.getUser().getId().equals(user.getId()) &&
            !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Unauthorized to view this order");
        }
        return convertToResponse(order);
    }
    @Override
    public Page<OrderResponse> getUserOrder(Pageable pageable) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        return orderRepository.findByUser(user, pageable)
                .map(this::convertToResponse);
    }
    @Transactional
    @Override
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Order not found with id: " + id, "ORDER_NOT_FOUND"
        ));
        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            if(newStatus == Order.OrderStatus.CANCELLED ||
                    newStatus == Order.OrderStatus.REFUNDED ) {
                // restore product quantities
                for(OrderItem item : order.getItems()) {
                    Product product = item.getProduct();
                    product.setQuantity(product.getQuantity() + item.getQuantity());
                    productRepository.save(product);
                }
            }
            order = orderRepository.save(order);
            return convertToResponse(order);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    @Override
    public Page<OrderResponse> getAllUsersOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Override
    public OrderResponse cancelOrder(Long id) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Order not found with id: " + id, "ORDER_NOT_FOUND"
        ));
        if(!order.getUser().getId().equals(user.getId()) &&
                !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Unauthorized to view this order");
        }
        if(!order.getStatus().equals(Order.OrderStatus.PENDING)) {
            throw new RuntimeException("present order status is " + order.getStatus()+". Cannot Cancel now.." );
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        return convertToResponse(order);
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingCharge(order.getShippingCharge());
        response.setTax(order.getTax());
        response.setFinalAmount(order.getFinalAmount());
        response.setShippingAddress(order.getShippingAddress());
        response.setBillingAddress(order.getBillingAddress());
        response.setStatus(order.getStatus().name());
        response.setPaymentMethod(order.getPaymentMethod().name());
        response.setPaymentStatus(order.getPaymentStatus().toString());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(order.getItems().stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setUnitPrice(item.getUnitPrice());
                    itemResponse.setTotalPrice(item.getTotalPrice());

                    ProductResponse productResponse = new ProductResponse();
                    productResponse.setId(item.getProduct().getId());
                    productResponse.setName(item.getProduct().getName());
                    productResponse.setPrice(item.getProduct().getPrice());
                    productResponse.setDiscountedPrice(item.getProduct().getDiscountedPrice());
                    productResponse.setQuantity(item.getProduct().getQuantity());
                    productResponse.setImageUrl(item.getProduct().getImageUrl());
                    productResponse.setBrand(item.getProduct().getBrand());
                    productResponse.setColor(item.getProduct().getColor());
                    productResponse.setDescription(item.getProduct().getDescription());
                    productResponse.setSize(item.getProduct().getSize());
                    productResponse.setRating(item.getProduct().getRating());
                    productResponse.setReviewCount(item.getProduct().getReviewCount());
                    productResponse.setCreatedAt(item.getProduct().getCreatedAt());

                    CategoryResponse categoryResponse = new CategoryResponse();
                    categoryResponse.setId(item.getProduct().getCategory().getId());
                    categoryResponse.setName(item.getProduct().getCategory().getName());
                    categoryResponse.setDescription(item.getProduct().getCategory().getDescription());
                    categoryResponse.setImageUrl(item.getProduct().getCategory().getImageUrl());
                    categoryResponse.setCreatedAt(item.getProduct().getCategory().getCreatedAt());

                    productResponse.setCategory(categoryResponse);

                    itemResponse.setProduct(productResponse);

                    return itemResponse;
                })
                .collect(java.util.stream.Collectors.toList()));

        return response;
    }
}
