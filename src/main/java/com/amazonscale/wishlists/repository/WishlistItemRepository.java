package com.amazonscale.wishlists.repository;

import com.amazonscale.wishlists.entity.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    boolean existsByWishlist_IdAndProduct_Id(Long wishlistId, Long productId);

    Optional<WishlistItem> findByWishlist_IdAndProduct_Id(Long wishlistId, Long productId);

    Page<WishlistItem> findAllByWishlist_IdOrderByCreatedAtDesc(Long wishlistId, Pageable pageable);

    long countByWishlist_Id(Long wishlistId);

    void deleteByWishlist_IdAndProduct_Id(Long wishlistId, Long productId);

    void deleteAllByWishlist_Id(Long wishlistId);

}