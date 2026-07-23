package com.amazonscale.user.service;

import com.amazonscale.user.dto.LoginRequest;
import com.amazonscale.user.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
