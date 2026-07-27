package com.amazonscale.user.controller;

import com.amazonscale.user.dto.LoginRequest;
import com.amazonscale.user.dto.LoginResponse;
import com.amazonscale.user.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should authenticate user successfully and return HTTP 200 OK with LoginResponse")
    void shouldLoginSuccessfully() throws Exception {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("password123")
                .build();

        LoginResponse loginResponse = new LoginResponse("mock_token_123", "Bearer");

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock_token_123"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when LoginRequest contains invalid email or password")
    void shouldReturnBadRequestWhenLoginRequestIsInvalid() throws Exception {
        // Arrange
        LoginRequest invalidRequest = LoginRequest.builder()
                .email("invalid-email-format")
                .password("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should build AuthController using Builder pattern")
    void shouldBuildAuthControllerUsingBuilder() {
        // Arrange & Act
        AuthController controller = AuthController.builder()
                .authService(authService)
                .build();

        // Assert
        org.assertj.core.api.Assertions.assertThat(controller).isNotNull();
    }
}