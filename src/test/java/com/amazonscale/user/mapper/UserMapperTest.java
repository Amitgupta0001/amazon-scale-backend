package com.amazonscale.user.mapper;

import com.amazonscale.user.dto.UserRequest;
import com.amazonscale.user.dto.UserResponse;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserMapperTest {

    @Test
    @DisplayName("Should correctly map UserRequest DTO to User entity")
    void shouldMapUserRequestToUserEntity() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .firstName("Bob")
                .lastName("Marley")
                .email("bob@example.com")
                .password("pass1234")
                .role(Role.SELLER)
                .build();

        // Act
        User user = UserMapper.toEntity(request);

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("Bob");
        assertThat(user.getLastName()).isEqualTo("Marley");
        assertThat(user.getEmail()).isEqualTo("bob@example.com");
        assertThat(user.getPassword()).isEqualTo("pass1234");
        assertThat(user.getRole()).isEqualTo(Role.SELLER);
    }

    @Test
    @DisplayName("Should correctly map User entity to UserResponse DTO")
    void shouldMapUserEntityToUserResponseDto() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(10L)
                .firstName("Bob")
                .lastName("Marley")
                .email("bob@example.com")
                .role(Role.SELLER)
                .enabled(true)
                .createdAt(now)
                .build();

        // Act
        UserResponse response = UserMapper.toResponse(user);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getFirstName()).isEqualTo("Bob");
        assertThat(response.getLastName()).isEqualTo("Marley");
        assertThat(response.getEmail()).isEqualTo("bob@example.com");
        assertThat(response.getRole()).isEqualTo(Role.SELLER);
        assertThat(response.isEnabled()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should throw UnsupportedOperationException when invoking private constructor via reflection")
    void shouldThrowUnsupportedOperationExceptionWhenInstantiatingPrivateConstructor() throws Exception {
        // Arrange
        Constructor<UserMapper> constructor = UserMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Act & Assert
        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }
}