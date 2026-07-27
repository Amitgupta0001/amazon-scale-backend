package com.amazonscale.wishlists.repository;

import com.amazonscale.wishlists.entity.Wishlist;
import com.amazonscale.wishlists.enums.WishlistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByIdAndUser_Id(Long wishlistId, Long userId);

    boolean existsByUser_IdAndNameIgnoreCase(Long userId, String name);

    Optional<Wishlist> findByUser_IdAndNameIgnoreCase(Long userId, String name);

    List<Wishlist> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    Page<Wishlist> findAllByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Wishlist> findAllByUser_IdAndType(Long userId, WishlistType type);

    long countByUser_Id(Long userId);

    void deleteByIdAndUser_Id(Long wishlistId, Long userId);

}