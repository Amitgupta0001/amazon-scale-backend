package com.amazonscale.user.service;

import com.amazonscale.user.dto.UserRequest;
import com.amazonscale.user.dto.UserResponse;

public interface UserService {
    UserResponse register(UserRequest request);
}
