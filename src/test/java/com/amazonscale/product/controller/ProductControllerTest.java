package com.amazonscale.product.controller;

import com.amazonscale.common.response.PageResponse;
import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.dto.SearchSuggestionResponse;
import com.amazonscale.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;
    private ProductResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();

        sampleResponse = ProductResponse.builder()
                .id(1L)
                .name("Smartphone")
                .description("Latest smartphone")
                .imageUrl("https://example.com/image.jpg")
                .price(new BigDecimal("699.99"))
                .stock(50)
                .brand("TechBrand")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should create product successfully and return HTTP 201 Created with ProductResponse")
    void shouldCreateProductSuccessfully() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Smartphone");
        request.setDescription("Latest smartphone");
        request.setImageUrl("https://example.com/image.jpg");
        request.setPrice(new BigDecimal("699.99"));
        request.setStock(50);
        request.setBrand("TechBrand");

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Smartphone"))
                .andExpect(jsonPath("$.brand").value("TechBrand"));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when creating product with missing required fields")
    void shouldReturnBadRequestWhenCreateProductValidationFails() throws Exception {
        ProductRequest request = new ProductRequest();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("Should return product by ID and HTTP 200 OK when product exists")
    void shouldGetProductByIdSuccessfully() throws Exception {
        when(productService.getProduct(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Smartphone"))
                .andExpect(jsonPath("$.brand").value("TechBrand"));

        verify(productService, times(1)).getProduct(1L);
    }

    @Test
    @DisplayName("Should search and filter products with pagination and return HTTP 200 OK")
    void shouldSearchProductsSuccessfully() throws Exception {
        PageResponse<ProductResponse> pageResponse = PageResponse.from(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 12), 1));

        when(productService.searchProducts(
                any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/products?q=Phone&brand=TechBrand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Smartphone"));

        verify(productService, times(1)).searchProducts(
                any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Should return unpaginated products list via /all and HTTP 200 OK")
    void shouldGetAllProductsSuccessfully() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Smartphone"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Should return search suggestions and HTTP 200 OK")
    void shouldGetSearchSuggestionsSuccessfully() throws Exception {
        SearchSuggestionResponse suggestions = SearchSuggestionResponse.builder()
                .productNames(List.of("Smartphone"))
                .brands(List.of("TechBrand"))
                .categories(List.of("Electronics"))
                .build();

        when(productService.getSearchSuggestions("smart")).thenReturn(suggestions);

        mockMvc.perform(get("/api/v1/products/search/suggestions?q=smart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productNames[0]").value("Smartphone"))
                .andExpect(jsonPath("$.brands[0]").value("TechBrand"));

        verify(productService, times(1)).getSearchSuggestions("smart");
    }

    @Test
    @DisplayName("Should update product by ID and return HTTP 200 OK")
    void shouldUpdateProductSuccessfully() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Smartphone Pro");
        request.setDescription("Pro model");
        request.setImageUrl("https://example.com/image.jpg");
        request.setPrice(new BigDecimal("899.99"));
        request.setStock(40);
        request.setBrand("TechBrand");

        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    @DisplayName("Should delete product by ID and return HTTP 204 No Content")
    void shouldDeleteProductSuccessfully() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should build ProductController using Builder pattern")
    void shouldBuildProductControllerUsingBuilder() {
        ProductController controller = ProductController.builder()
                .productService(productService)
                .build();

        org.assertj.core.api.Assertions.assertThat(controller).isNotNull();
    }
}