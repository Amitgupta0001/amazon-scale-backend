package com.amazonscale.category.service.impl;

import com.amazonscale.category.dto.CategoryResponse;
import com.amazonscale.category.dto.CreateCategoryRequest;
import com.amazonscale.category.dto.UpdateCategoryRequest;
import com.amazonscale.category.entity.Category;
import com.amazonscale.category.exception.CategoryAlreadyExistsException;
import com.amazonscale.category.exception.CategoryNotFoundException;
import com.amazonscale.category.exception.InvalidCategoryHierarchyException;
import com.amazonscale.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private Category parentCategory;
    private CreateCategoryRequest createRequest;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Smartphones")
                .description("Smartphones category")
                .build();

        parentCategory = Category.builder()
                .id(2L)
                .name("Electronics")
                .description("Electronics parent")
                .build();

        createRequest = new CreateCategoryRequest();
        createRequest.setName("Smartphones");
        createRequest.setDescription("Smartphones category");
    }

    @Test
    void createCategorySuccessWithoutParent() {
        // Arrange
        when(repository.existsByName("Smartphones")).thenReturn(false);
        when(repository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponse response = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Smartphones", response.getName());
        verify(repository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategorySuccessWithParent() {
        // Arrange
        createRequest.setParentCategoryId(2L);
        when(repository.existsByName("Smartphones")).thenReturn(false);
        when(repository.findById(2L)).thenReturn(Optional.of(parentCategory));
        when(repository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponse response = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(response);
        verify(repository, times(1)).findById(2L);
    }

    @Test
    void shouldThrowCategoryAlreadyExistsWhenCreatingDuplicate() {
        // Arrange
        when(repository.existsByName("Smartphones")).thenReturn(true);

        // Act & Assert
        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(createRequest));
    }

    @Test
    void getCategoryByIdSuccess() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        CategoryResponse response = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowCategoryNotFoundWhenGetByIdFails() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }

    @Test
    void getAllCategoriesSuccess() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(category));

        // Act
        List<CategoryResponse> responses = categoryService.getAllCategories();

        // Assert
        assertEquals(1, responses.size());
        assertEquals("Smartphones", responses.get(0).getName());
    }

    @Test
    void updateCategorySuccess() {
        // Arrange
        UpdateCategoryRequest updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Smartphones Pro");
        updateRequest.setDescription("Updated description");

        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponse response = categoryService.updateCategory(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Smartphones Pro", category.getName());
    }

    @Test
    void shouldThrowInvalidCategoryHierarchyWhenParentIsSelf() {
        // Arrange
        UpdateCategoryRequest updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Smartphones");
        updateRequest.setParentCategoryId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(category));

        // Act & Assert
        assertThrows(InvalidCategoryHierarchyException.class, () -> categoryService.updateCategory(1L, updateRequest));
    }

    @Test
    void deleteCategorySuccess() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(repository, times(1)).delete(category);
    }
}