package com.amazonscale.category.mapper;

import com.amazonscale.category.dto.CategoryResponse;
import com.amazonscale.category.dto.CreateCategoryRequest;
import com.amazonscale.category.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {
        // Utility class
    }

    public static Category toCategory(CreateCategoryRequest request) {

        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        return category;
    }

    public static CategoryResponse toResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentCategoryId(
                        category.getParentCategory() != null
                                ? category.getParentCategory().getId()
                                : null
                )
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}