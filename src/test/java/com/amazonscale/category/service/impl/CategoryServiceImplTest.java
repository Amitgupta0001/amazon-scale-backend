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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic gadgets")
                .imageUrl("https://example.com/electronics.jpg")
                .build();

        createRequest = new CreateCategoryRequest();
        createRequest.setName("Electronics");
        createRequest.setDescription("Electronic gadgets");
        createRequest.setImageUrl("https://example.com/electronics.jpg");

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Electronics");
        updateRequest.setDescription("Updated description");
        updateRequest.setImageUrl("https://example.com/electronics.jpg");
    }

    @Test
    @DisplayName("Should create root category successfully when name is unique and parent ID is null")
    void shouldCreateRootCategorySuccessfully() {
        // Arrange
        when(repository.existsByName("Electronics")).thenReturn(false);
        when(repository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponse response = categoryService.createCategory(createRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Electronics");
        assertThat(response.getParentCategoryId()).isNull();

        verify(repository).existsByName("Electronics");
        verify(repository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should create child category successfully when parent category exists")
    void shouldCreateChildCategorySuccessfully() {
        // Arrange
        createRequest.setParentCategoryId(10L);
        Category parentCategory = Category.builder().id(10L).name("Parent").build();

        when(repository.existsByName("Electronics")).thenReturn(false);
        when(repository.findById(10L)).thenReturn(Optional.of(parentCategory));
        when(repository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponse response = categoryService.createCategory(createRequest);

        // Assert
        assertThat(response).isNotNull();
        verify(repository).findById(10L);
        verify(repository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw CategoryAlreadyExistsException when creating category with duplicate name")
    void shouldThrowCategoryAlreadyExistsExceptionWhenNameTaken() {
        // Arrange
        when(repository.existsByName("Electronics")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryService.createCategory(createRequest))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessageContaining("Electronics");

        verify(repository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when parent category ID does not exist on creation")
    void shouldThrowCategoryNotFoundExceptionWhenParentDoesNotExistOnCreate() {
        // Arrange
        createRequest.setParentCategoryId(99L);
        when(repository.existsByName("Electronics")).thenReturn(false);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.createCategory(createRequest))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should get category by ID successfully when category exists")
    void shouldGetCategoryByIdSuccessfully() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        CategoryResponse response = categoryService.getCategoryById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Electronics");

        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when getting category by non-existent ID")
    void shouldThrowCategoryNotFoundExceptionWhenCategoryDoesNotExist() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should get all categories as a list of CategoryResponse")
    void shouldGetAllCategoriesSuccessfully() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(category));

        // Act
        List<CategoryResponse> responses = categoryService.getAllCategories();

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Electronics");

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should update category successfully when payload is valid")
    void shouldUpdateCategorySuccessfully() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponse response = categoryService.updateCategory(1L, updateRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(repository).findById(1L);
        verify(repository).save(category);
    }

    @Test
    @DisplayName("Should throw InvalidCategoryHierarchyException when category is assigned as its own parent")
    void shouldThrowInvalidCategoryHierarchyExceptionWhenSelfParented() {
        // Arrange
        updateRequest.setParentCategoryId(1L); // Same as category ID (1L)
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateRequest))
                .isInstanceOf(InvalidCategoryHierarchyException.class)
                .hasMessageContaining("A category cannot be its own parent.");

        verify(repository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should delete category by ID when category exists")
    void shouldDeleteCategorySuccessfully() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(repository).findById(1L);
        verify(repository).delete(category);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when deleting non-existent category")
    void shouldThrowCategoryNotFoundExceptionWhenDeletingNonExistentCategory() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.deleteCategory(99L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).delete(any(Category.class));
    }

    @Test
    @DisplayName("Should build CategoryServiceImpl using Builder pattern")
    void shouldBuildCategoryServiceImplUsingBuilder() {
        // Arrange & Act
        CategoryServiceImpl service = CategoryServiceImpl.builder()
                .repository(repository)
                .build();

        // Assert
        assertThat(service).isNotNull();
    }
}