package com.amazonscale.category.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 200,message = "Category name cannot exceed 200 characters")
    private String name;

    @Size(max = 1000,message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 500,message = "URL cannot exceed 500 characters")
    private String imageUrl;

    private Long parentCategoryId;
}
