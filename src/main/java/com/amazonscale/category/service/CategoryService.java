package com.amazonscale.category.service;


import com.amazonscale.category.dto.CategoryResponse;
import com.amazonscale.category.dto.CreateCategoryRequest;
import com.amazonscale.category.dto.UpdateCategoryRequest;

import java.util.List;
public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long categoryId,UpdateCategoryRequest request);

    void deleteCategory(Long id);

    CategoryResponse getCategoryById(Long id);

    List<CategoryResponse> getAllCategories();
}
