package com.amazonscale.category.controller;

import com.amazonscale.category.dto.CategoryResponse;
import com.amazonscale.category.dto.CreateCategoryRequest;
import com.amazonscale.category.dto.UpdateCategoryRequest;
import com.amazonscale.category.service.CategoryService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private ObjectMapper objectMapper;
    private CategoryResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();

        sampleResponse = CategoryResponse.builder()
                .id(1L)
                .name("Electronics")
                .description("Gadgets & Devices")
                .imageUrl("https://example.com/electronics.jpg")
                .parentCategoryId(null)
                .build();
    }

    @Test
    @DisplayName("Should create category successfully and return HTTP 201 Created with CategoryResponse")
    void shouldCreateCategorySuccessfully() throws Exception {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");
        request.setDescription("Gadgets & Devices");
        request.setImageUrl("https://example.com/electronics.jpg");

        when(categoryService.createCategory(any(CreateCategoryRequest.class))).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService, times(1)).createCategory(any(CreateCategoryRequest.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when CreateCategoryRequest fails validation")
    void shouldReturnBadRequestWhenCreateCategoryValidationFails() throws Exception {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest(); // blank name

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any(CreateCategoryRequest.class));
    }

    @Test
    @DisplayName("Should get category by ID and return HTTP 200 OK")
    void shouldGetCategoryByIdSuccessfully() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    @DisplayName("Should get all categories list and return HTTP 200 OK")
    void shouldGetAllCategoriesSuccessfully() throws Exception {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(List.of(sampleResponse));

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("Should update category by ID and return HTTP 200 OK")
    void shouldUpdateCategorySuccessfully() throws Exception {
        // Arrange
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Updated Electronics");
        request.setDescription("Updated desc");

        when(categoryService.updateCategory(eq(1L), any(UpdateCategoryRequest.class))).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(UpdateCategoryRequest.class));
    }

    @Test
    @DisplayName("Should delete category by ID and return HTTP 204 No Content")
    void shouldDeleteCategorySuccessfully() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    @DisplayName("Should build CategoryController using Builder pattern")
    void shouldBuildCategoryControllerUsingBuilder() {
        // Arrange & Act
        CategoryController controller = CategoryController.builder()
                .categoryService(categoryService)
                .build();

        // Assert
        org.assertj.core.api.Assertions.assertThat(controller).isNotNull();
    }
}