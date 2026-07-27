package com.amazonscale.cart.repository;

import com.amazonscale.cart.entity.Cart;
import com.amazonscale.user.enums.Role;
import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("Should find Cart by User ID when cart exists")
    void shouldFindCartByUserId() {
        // Arrange
        User user = User.builder()
                .firstName("Cart")
                .lastName("Tester")
                .email("carttester@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        Cart cart = Cart.builder()
                .user(savedUser)
                .build();
        entityManager.persistAndFlush(cart);

        // Act
        Optional<Cart> found = cartRepository.findByUser_Id(savedUser.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Should return true for existsByUser_Id when cart exists")
    void shouldReturnTrueWhenCartExistsByUserId() {
        // Arrange
        User user = User.builder()
                .firstName("Cart")
                .lastName("Tester2")
                .email("carttester2@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        Cart cart = Cart.builder()
                .user(savedUser)
                .build();
        entityManager.persistAndFlush(cart);

        // Act
        boolean exists = cartRepository.existsByUser_Id(savedUser.getId());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false for existsByUser_Id when cart does not exist")
    void shouldReturnFalseWhenCartDoesNotExistByUserId() {
        // Act
        boolean exists = cartRepository.existsByUser_Id(999L);

        // Assert
        assertThat(exists).isFalse();
    }
}
