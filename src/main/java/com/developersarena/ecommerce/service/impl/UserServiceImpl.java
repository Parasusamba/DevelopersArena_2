package com.developersarena.ecommerce.service.impl;

import com.developersarena.ecommerce.dto.UserProfileResponse;
import com.developersarena.ecommerce.entity.User;
import com.developersarena.ecommerce.exception.NotFoundException;
import com.developersarena.ecommerce.repository.UserRepository;
import com.developersarena.ecommerce.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        return convertToResponse(user);
    }

    @Transactional
    @Override
    public UserProfileResponse updateUserProfile(UserProfileResponse updateRequest) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email,
                        "USER_NOT_FOUND"));
        Optional.ofNullable(updateRequest.getFirstName()).ifPresent(updateRequest::setFirstName);
        Optional.ofNullable(updateRequest.getLastName()).ifPresent(updateRequest::setLastName);
        Optional.ofNullable(updateRequest.getPhone()).ifPresent(updateRequest::setPhone);
        Optional.ofNullable(updateRequest.getAddress()).ifPresent(updateRequest::setAddress);
        Optional.ofNullable(updateRequest.getCity()).ifPresent(updateRequest::setCity);
        Optional.ofNullable(updateRequest.getState()).ifPresent(updateRequest::setState);
        Optional.ofNullable(updateRequest.getPincode()).ifPresent(updateRequest::setPincode);
        user = userRepository.save(user);
        return convertToResponse(user);
    }

    private UserProfileResponse convertToResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setPincode(user.getPincode());
        return response;
    }
}
