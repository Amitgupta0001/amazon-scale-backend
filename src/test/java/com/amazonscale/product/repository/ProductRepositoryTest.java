package com.amazonscale.product.repository;

import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should save and find Product by ID successfully")
    void shouldSaveAndFindProductById() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Tablet")
                .description("10-inch screen tablet")
                .imageUrl("https://example.com/tablet.jpg")
                .price(new BigDecimal("299.99"))
                .stock(25)
                .brand("TechCorp")
                .active(true)
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> foundProduct = productRepository.findById(1L);

        // Assert
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Tablet");
        assertThat(foundProduct.get().getPrice()).isEqualByComparingTo(new BigDecimal("299.99"));
    }

    @Test
    @DisplayName("Should return empty Optional when Product with given ID does not exist")
    void shouldReturnEmptyWhenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Assert
        assertThat(foundProduct).isEmpty();
    }

    @Test
    @DisplayName("Should find all Products saved in database")
    void shouldFindAllProducts() {
        // Arrange
        Product p1 = Product.builder().name("P1").description("D1").imageUrl("http://img1.jpg").price(new BigDecimal("10.00")).stock(5).brand("B1").active(true).build();
        Product p2 = Product.builder().name("P2").description("D2").imageUrl("http://img2.jpg").price(new BigDecimal("20.00")).stock(10).brand("B2").active(true).build();
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertThat(products).hasSize(2);
    }

    @Test
    @DisplayName("Should delete Product successfully from database")
    void shouldDeleteProductSuccessfully() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("ToDelete")
                .description("Desc")
                .imageUrl("https://example.com/delete.jpg")
                .price(new BigDecimal("50.00"))
                .stock(1)
                .brand("Brand")
                .active(true)
                .build();
        doNothing().when(productRepository).delete(product);

        // Act
        productRepository.delete(product);

        // Assert
        Optional<Product> found = productRepository.findById(1L);
        assertThat(found).isEmpty();
    }
}
