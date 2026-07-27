package com.amazonscale.product.service.impl;

import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest request;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Tablet")
                .description("10-inch Tablet")
                .imageUrl("https://example.com/tablet.jpg")
                .price(new BigDecimal("299.99"))
                .stock(20)
                .brand("TechBrand")
                .active(true)
                .build();

        request = new ProductRequest();
        request.setName("Tablet");
        request.setDescription("10-inch Tablet");
        request.setImageUrl("https://example.com/tablet.jpg");
        request.setPrice(new BigDecimal("299.99"));
        request.setStock(20);
        request.setBrand("TechBrand");
    }

    @Test
    @DisplayName("Should successfully create a new product and return ProductResponse")
    void shouldCreateProductSuccessfully() {
        // Arrange
        when(repository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse response = productService.createProduct(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Tablet");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("299.99"));

        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should successfully get product by ID when product exists")
    void shouldGetProductByIdSuccessfully() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponse response = productService.getProduct(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Tablet");

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when getting product by non-existent ID")
    void shouldThrowProductNotFoundExceptionWhenGettingNonExistentProduct() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProduct(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found with id :99");

        verify(repository).findById(99L);
    }

    @Test
    @DisplayName("Should return list of ProductResponse for all existing products")
    void shouldGetAllProductsSuccessfully() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(product));

        // Act
        List<ProductResponse> responses = productService.getAllProducts();

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getName()).isEqualTo("Tablet");

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should update product fields successfully when product exists")
    void shouldUpdateProductSuccessfully() {
        // Arrange
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Tablet");
        updateRequest.setDescription("Updated description");
        updateRequest.setImageUrl("https://example.com/updated.jpg");
        updateRequest.setPrice(new BigDecimal("349.99"));
        updateRequest.setStock(15);
        updateRequest.setBrand("TechBrandPro");

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse response = productService.updateProduct(1L, updateRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(repository).findById(1L);
        verify(repository).save(product);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existent product")
    void shouldThrowProductNotFoundExceptionWhenUpdatingNonExistentProduct() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(99L, request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository).findById(99L);
        verify(repository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product successfully when product exists")
    void shouldDeleteProductSuccessfully() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(repository).findById(1L);
        verify(repository).delete(product);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deleting non-existent product")
    void shouldThrowProductNotFoundExceptionWhenDeletingNonExistentProduct() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository).findById(99L);
        verify(repository, never()).delete(any(Product.class));
    }

    @Test
    @DisplayName("Should build ProductServiceImpl using Builder pattern")
    void shouldBuildProductServiceImplUsingBuilder() {
        // Arrange & Act
        ProductServiceImpl service = ProductServiceImpl.builder()
                .repository(repository)
                .build();

        // Assert
        assertThat(service).isNotNull();
    }
}