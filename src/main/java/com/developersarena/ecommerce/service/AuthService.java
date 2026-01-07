package com.developersarena.ecommerce.service;

import com.developersarena.ecommerce.dto.AuthRequest;
import com.developersarena.ecommerce.dto.AuthResponse;
import com.developersarena.ecommerce.dto.RegistrationRequest;

public interface AuthService {

    AuthResponse register(RegistrationRequest request);

    AuthResponse login(AuthRequest request);
}
