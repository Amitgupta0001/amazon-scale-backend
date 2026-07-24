package com.amazonscale.category.service.impl;


import com.amazonscale.category.dto.CategoryResponse;
import com.amazonscale.category.dto.CreateCategoryRequest;
import com.amazonscale.category.dto.UpdateCategoryRequest;
import com.amazonscale.category.entity.Category;
import com.amazonscale.category.exception.CategoryAlreadyExistsException;
import com.amazonscale.category.exception.CategoryNotFoundException;
import com.amazonscale.category.exception.InvalidCategoryHierarchyException;
import com.amazonscale.category.mapper.CategoryMapper;
import com.amazonscale.category.repository.CategoryRepository;
import com.amazonscale.category.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }


    //CREATE CATEGORY
    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        if (repository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException(request.getName());
        }

        Category category = CategoryMapper.toCategory(request);

        if (request.getParentCategoryId() != null) {
            Category parent = repository.findById(request.getParentCategoryId())
                    .orElseThrow(() ->
                            new CategoryNotFoundException(request.getParentCategoryId()));

            category.setParentCategory(parent);
        }

        Category savedCategory = repository.save(category);

        return CategoryMapper.toResponse(savedCategory);
    }

    //GET CATEGORY
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category = repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return CategoryMapper.toResponse(category);
    }

    //GET ALL CATEGORIES
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(){
        return repository.findAll()
                .stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    //UPDATE CATEGORY
    @Override
    public CategoryResponse updateCategory(Long id,
                                           UpdateCategoryRequest request) {

        Category category = repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getName().equalsIgnoreCase(request.getName())
                && repository.existsByName(request.getName())) {

            throw new CategoryAlreadyExistsException(request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        if (request.getParentCategoryId() != null) {

            if (id.equals(request.getParentCategoryId())) {
                throw new InvalidCategoryHierarchyException();
            }

            Category parent = repository.findById(request.getParentCategoryId())
                    .orElseThrow(() ->
                            new CategoryNotFoundException(request.getParentCategoryId()));

            category.setParentCategory(parent);

        } else {
            category.setParentCategory(null);
        }

        Category updated = repository.save(category);

        return CategoryMapper.toResponse(updated);
    }

    @Override
    public void deleteCategory(Long id){
        Category category = repository.findById(id)
                .orElseThrow(()-> new CategoryNotFoundException(id));

        repository.delete(category);
    }
}
