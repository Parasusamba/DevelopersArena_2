package com.developersarena.ecommerce.service.impl;

import com.developersarena.ecommerce.dto.AuthRequest;
import com.developersarena.ecommerce.dto.AuthResponse;
import com.developersarena.ecommerce.dto.RegistrationRequest;
import com.developersarena.ecommerce.entity.Cart;
import com.developersarena.ecommerce.entity.User;
import com.developersarena.ecommerce.exception.NotFoundException;
import com.developersarena.ecommerce.exception.UserAlreadyExistsException;
import com.developersarena.ecommerce.repository.CartRepository;
import com.developersarena.ecommerce.repository.UserRepository;
import com.developersarena.ecommerce.secutity.JwtService;
import com.developersarena.ecommerce.service.AuthService;
import com.developersarena.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CartRepository cartRepository;
    @Override
    public AuthResponse register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName() != null ? request.getLastName() : " ");
        user.setActive(true);
        user.setRole(User.Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPincode(request.getPincode());

        user = userRepository.save(user);

        // create cart for user
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(),
                String.valueOf(user.getRole()),
                user.getFirstName(), user.getFirstName());
    }
    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + request.getEmail(), "USER_NOT_FOUND"));
        if(!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(),
                String.valueOf(user.getRole()),
                user.getFirstName(), user.getLastName());
    }
}
