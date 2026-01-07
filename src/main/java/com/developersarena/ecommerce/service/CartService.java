package com.developersarena.ecommerce.service;

import com.developersarena.ecommerce.dto.CartItemRequest;
import com.developersarena.ecommerce.dto.CartResponse;
import com.developersarena.ecommerce.entity.User;
import jakarta.transaction.Transactional;

public interface CartService {

    CartResponse addToCart(CartItemRequest request);

    CartResponse getCart();

    CartResponse updateCartItem(Long itemId, Integer quantity);

    void removeCartItem(Long itemId);

    void clearCart();

}
