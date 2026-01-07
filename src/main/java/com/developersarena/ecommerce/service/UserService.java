package com.developersarena.ecommerce.service;

import com.developersarena.ecommerce.dto.UserProfileResponse;
import jakarta.transaction.Transactional;

public interface UserService {
    UserProfileResponse getCurrentUserProfile();

    UserProfileResponse updateUserProfile(UserProfileResponse updateRequest);
}
