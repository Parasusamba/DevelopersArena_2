package com.developersarena.ecommerce.controller;


import com.developersarena.ecommerce.dto.AuthRequest;
import com.developersarena.ecommerce.dto.AuthResponse;
import com.developersarena.ecommerce.dto.RegistrationRequest;
import com.developersarena.ecommerce.entity.User;
import com.developersarena.ecommerce.exception.ExceptionDTO;
import com.developersarena.ecommerce.exception.GlobalExceptionHandler;
import com.developersarena.ecommerce.exception.NotFoundException;
import com.developersarena.ecommerce.exception.UserAlreadyExistsException;
import com.developersarena.ecommerce.repository.UserRepository;
import com.developersarena.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
