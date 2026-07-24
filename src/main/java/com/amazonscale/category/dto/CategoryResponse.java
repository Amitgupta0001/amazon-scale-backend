package com.amazonscale.category.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long parentCategoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
