package com.developersarena.ecommerce.service.impl;

import com.developersarena.ecommerce.dto.CartItemRequest;
import com.developersarena.ecommerce.dto.CartItemResponse;
import com.developersarena.ecommerce.dto.CartResponse;
import com.developersarena.ecommerce.dto.ProductResponse;
import com.developersarena.ecommerce.entity.Cart;
import com.developersarena.ecommerce.entity.CartItem;
import com.developersarena.ecommerce.entity.Product;
import com.developersarena.ecommerce.entity.User;
import com.developersarena.ecommerce.exception.NotFoundException;
import com.developersarena.ecommerce.repository.CartRepository;
import com.developersarena.ecommerce.repository.ProductRepository;
import com.developersarena.ecommerce.repository.UserRepository;
import com.developersarena.ecommerce.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public CartResponse addToCart(CartItemRequest request) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(
                () -> new NotFoundException("Cart not found ", "CART_NOT_FOUND")
        );
        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + request.getProductId(),
                        "PRODUCT_NOT_FOUND")
        );
        if(product.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Product quantity less than requested quantity");
        }
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
        //BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        if(existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.calculateTotalPrice();
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.calculateTotalPrice();
            cart.getItems().add(newItem);
        }
        cart.calculateTotalPrice();;
        cart = cartRepository.save(cart);
        return convertToResponse(cart);
    }

    @Override
    public CartResponse getCart() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(
                () -> new NotFoundException("Cart not found ", "CART_NOT_FOUND")
        );
        return convertToResponse(cart);
    }
    @Transactional
    @Override
    public CartResponse updateCartItem(Long itemId, Integer quantity) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(
                () -> new NotFoundException("Cart not found ", "CART_NOT_FOUND")
        );

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Cart item not found with id: " + itemId, "CART_ITEM_NOT_FOUND"
                ));
        if(cartItem.getProduct().getQuantity() < quantity) {
            throw new RuntimeException("Product quantity less than requested quantity");
        }
        cartItem.setQuantity(quantity);
        cart.calculateTotalPrice();
        cart = cartRepository.save(cart);
        return convertToResponse(cart);
    }

    @Transactional
    @Override
    public void removeCartItem(Long itemId) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(
                () -> new NotFoundException("Cart not found ", "CART_NOT_FOUND")
        );
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }
    @Transactional
    @Override
    public void clearCart() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(
                () -> new NotFoundException("Cart not found ", "CART_NOT_FOUND")
        );
        cart.getItems().clear();
        cart.setTotalPrice(null);
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    private CartResponse convertToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotalPrice(cart.getTotalPrice());
        response.setUpdateAt(cart.getUpdatedAt());
        response.setItems(cart.getItems().stream()
                .map(item -> {
                    CartItemResponse itemResponse = new CartItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setTotalPrice(item.getTotalPrice());

                    ProductResponse productResponse = new ProductResponse();
                    productResponse.setId(item.getProduct().getId());
                    productResponse.setName(item.getProduct().getName());
                    productResponse.setPrice(item.getProduct().getPrice());
                    productResponse.setDiscountedPrice(item.getProduct().getDiscountedPrice());
                    productResponse.setImageUrl(item.getProduct().getImageUrl());
                    productResponse.setBrand(item.getProduct().getBrand());
                    itemResponse.setProduct(productResponse);
                    return itemResponse;
                })
                .collect(Collectors.toList())
        );
        return response;
    }

}
