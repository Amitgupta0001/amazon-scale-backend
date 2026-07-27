package com.amazonscale.user.controller;

import com.amazonscale.user.dto.UserRequest;
import com.amazonscale.user.dto.UserResponse;
import com.amazonscale.user.enums.Role;
import com.amazonscale.user.exception.EmailAlreadyExistsException;
import com.amazonscale.user.service.UserService;
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
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should register user successfully and return HTTP 201 Created with UserResponse")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Arrange
        UserRequest request = UserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .role(Role.CUSTOMER)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        when(userService.register(any(UserRequest.class))).thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.enabled").value(true));

        verify(userService, times(1)).register(any(UserRequest.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when UserRequest fails bean validation")
    void shouldReturnBadRequestWhenUserRequestIsInvalid() throws Exception {
        // Arrange
        UserRequest invalidRequest = UserRequest.builder()
                .firstName("") // Blank
                .lastName("Doe")
                .email("invalid-email") // Invalid format
                .password("short") // Password < 8 chars
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any(UserRequest.class));
    }

    @Test
    @DisplayName("Should build UserController using Builder pattern")
    void shouldBuildUserControllerUsingBuilder() {
        // Arrange & Act
        UserController controller = UserController.builder()
                .userService(userService)
                .build();

        // Assert
        org.assertj.core.api.Assertions.assertThat(controller).isNotNull();
    }
}