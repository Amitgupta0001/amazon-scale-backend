package com.amazonscale.category.controller;


import com.amazonscale.category.dto.CategoryResponse;
import com.amazonscale.category.dto.CreateCategoryRequest;
import com.amazonscale.category.dto.UpdateCategoryRequest;
import com.amazonscale.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Categories",
        description = "Category Management APIs"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    //CREATE CATEGORY
    @Operation(
            summary = "Create a new category",
            description = "Creates a new category with an optional parent category."
    )
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request){

        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //GET CATEGORY BY ID
    @Operation(
            summary = "Get category by ID",
            description = "Returns the category associated with the given ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable("id") Long categoryId){
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    //GET ALL CATEGORIES
    @Operation(
            summary = "Get all categories",
            description = "Returns a list of all available categories."
    )
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    //UPDATE CATEGORIES
    @Operation(
            summary = "Update a category",
            description = "Updates the details of an existing category."
    )
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable("id") Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request){

        return ResponseEntity.ok(categoryService.updateCategory(categoryId,request));
    }

    //DELETE CATEGORY
    @Operation(
            summary = "Delete a category",
            description = "Deletes the category with the specified ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long categoryId){
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build(); // 204 No Content is the standard response for successful delete
    }


}
