package com.amazonscale.product.mapper;

import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.entity.Product;

import java.math.BigDecimal;
import java.util.ArrayList;

@lombok.Builder
public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toEntity(ProductRequest request) {
        if (request == null) return null;

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice() != null ? request.getOriginalPrice() : request.getPrice());
        product.setDiscountPercentage(request.getDiscountPercentage() != null ? request.getDiscountPercentage() : BigDecimal.ZERO);
        product.setStock(request.getStock());
        product.setBrand(request.getBrand());
        product.setRating(request.getRating() != null ? request.getRating() : BigDecimal.valueOf(4.5));
        product.setReviewCount(request.getReviewCount() != null ? request.getReviewCount() : 0);
        product.setSku(request.getSku());
        product.setSlug(request.getSlug());
        product.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        product.setFeatured(request.getFeatured() != null ? request.getFeatured() : false);
        product.setThumbnail(request.getThumbnail() != null ? request.getThumbnail() : request.getImageUrl());
        product.setGalleryImages(request.getGalleryImages() != null ? request.getGalleryImages() : new ArrayList<>());

        return product;
    }

    public static ProductResponse toResponse(Product product) {
        if (product == null) return null;

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setImageUrl(product.getImageUrl());
        response.setPrice(product.getPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setDiscountPercentage(product.getDiscountPercentage());
        response.setStock(product.getStock());
        response.setBrand(product.getBrand());
        response.setActive(product.getActive());
        response.setRating(product.getRating());
        response.setReviewCount(product.getReviewCount());
        response.setSku(product.getSku());
        response.setSlug(product.getSlug());
        response.setStatus(product.getStatus());
        response.setFeatured(product.getFeatured());
        response.setThumbnail(product.getThumbnail());
        response.setGalleryImages(product.getGalleryImages());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        return response;
    }
}
