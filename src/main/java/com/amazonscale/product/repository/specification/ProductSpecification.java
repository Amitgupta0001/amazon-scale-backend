package com.amazonscale.product.repository.specification;

import com.amazonscale.product.entity.Product;
import jakarta.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> hasKeyword(String q) {
        return (root, query, cb) -> {
            if (q == null || q.trim().isEmpty()) return null;
            String term = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), term),
                    cb.like(cb.lower(root.get("brand")), term),
                    cb.like(cb.lower(root.get("description")), term)
            );
        };
    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.trim().isEmpty() || "all".equalsIgnoreCase(category.trim())) {
                return null;
            }
            String term = category.trim().toLowerCase();

            try {
                Long categoryId = Long.parseLong(term);
                return cb.equal(root.join("category", JoinType.LEFT).get("id"), categoryId);
            } catch (NumberFormatException e) {
                String matchPattern = "%" + term + "%";
                return cb.or(
                        cb.like(cb.lower(root.join("category", JoinType.LEFT).get("name")), matchPattern),
                        cb.like(cb.lower(root.get("brand")), matchPattern),
                        cb.like(cb.lower(root.get("name")), matchPattern)
                );
            }
        };
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, query, cb) -> {
            if (brand == null || brand.trim().isEmpty() || "all".equalsIgnoreCase(brand.trim())) {
                return null;
            }
            return cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase());
        };
    }

    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return null;
            return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) return null;
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> isInStock(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null || !inStock) return null;
            return cb.and(
                    cb.greaterThan(root.get("stock"), 0),
                    cb.equal(root.get("active"), true)
            );
        };
    }

    public static Specification<Product> isFeatured(Boolean featured) {
        return (root, query, cb) -> {
            if (featured == null) return null;
            return cb.equal(root.get("featured"), featured);
        };
    }

    public static Specification<Product> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return null;
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<Product> buildSpecification(
            String q,
            String category,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Boolean featured,
            Boolean active
    ) {
        return Specification
                .where(hasKeyword(q))
                .and(hasCategory(category))
                .and(hasBrand(brand))
                .and(hasMinPrice(minPrice))
                .and(hasMaxPrice(maxPrice))
                .and(isInStock(inStock))
                .and(isFeatured(featured))
                .and(isActive(active));
    }
}
