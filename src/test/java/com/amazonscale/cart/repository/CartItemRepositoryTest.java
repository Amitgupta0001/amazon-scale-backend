package com.amazonscale.cart.repository;

import com.amazonscale.cart.entity.Cart;
import com.amazonscale.cart.entity.CartItem;
import com.amazonscale.product.entity.Product;
import com.amazonscale.user.enums.Role;
import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Should find CartItem by Cart ID and Product ID")
    void shouldFindCartItemByCartIdAndProductId() {
        // Arrange
        User user = User.builder()
                .firstName("Item")
                .lastName("Tester")
                .email("itemtester@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        Cart cart = Cart.builder().user(savedUser).build();
        Cart savedCart = entityManager.persistAndFlush(cart);

        Product product = Product.builder()
                .name("Cart Product")
                .description("Desc")
                .imageUrl("http://img.jpg")
                .price(new BigDecimal("19.99"))
                .stock(100)
                .brand("Brand")
                .active(true)
                .build();
        Product savedProduct = entityManager.persistAndFlush(product);

        CartItem cartItem = CartItem.builder()
                .cart(savedCart)
                .product(savedProduct)
                .quantity(2)
                .priceAtAddition(new BigDecimal("19.99"))
                .build();
        entityManager.persistAndFlush(cartItem);

        // Act
        Optional<CartItem> found = cartItemRepository.findByCart_IdAndProduct_Id(savedCart.getId(),
                savedProduct.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should check existsByCart_IdAndProduct_Id correctly")
    void shouldCheckExistsByCartIdAndProductId() {
        // Arrange
        User user = User.builder()
                .firstName("Item")
                .lastName("Tester2")
                .email("itemtester2@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        Cart cart = Cart.builder().user(savedUser).build();
        Cart savedCart = entityManager.persistAndFlush(cart);

        Product product = Product.builder()
                .name("Cart Product 2")
                .description("Desc")
                .imageUrl("http://img.jpg")
                .price(new BigDecimal("29.99"))
                .stock(100)
                .brand("Brand")
                .active(true)
                .build();
        Product savedProduct = entityManager.persistAndFlush(product);

        CartItem cartItem = CartItem.builder()
                .cart(savedCart)
                .product(savedProduct)
                .quantity(1)
                .priceAtAddition(new BigDecimal("29.99"))
                .build();
        entityManager.persistAndFlush(cartItem);

        // Act
        boolean exists = cartItemRepository.existsByCart_IdAndProduct_Id(savedCart.getId(), savedProduct.getId());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find all CartItems by Cart ID")
    void shouldFindAllCartItemsByCartId() {
        // Arrange
        User user = User.builder()
                .firstName("Item")
                .lastName("Tester3")
                .email("itemtester3@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        Cart cart = Cart.builder().user(savedUser).build();
        Cart savedCart = entityManager.persistAndFlush(cart);

        Product product = Product.builder()
                .name("Cart Product 3")
                .description("Desc")
                .imageUrl("http://img.jpg")
                .price(new BigDecimal("39.99"))
                .stock(100)
                .brand("Brand")
                .active(true)
                .build();
        Product savedProduct = entityManager.persistAndFlush(product);

        CartItem cartItem = CartItem.builder()
                .cart(savedCart)
                .product(savedProduct)
                .quantity(3)
                .priceAtAddition(new BigDecimal("39.99"))
                .build();
        entityManager.persistAndFlush(cartItem);

        // Act
        List<CartItem> items = cartItemRepository.findByCart_Id(savedCart.getId());

        // Assert
        assertThat(items).hasSize(1);
    }
}
