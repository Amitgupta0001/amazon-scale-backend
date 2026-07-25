package com.amazonscale.cart.repository;

import com.amazonscale.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    Optional<CartItem> findByCart_IdAndProduct_Id(Long cartId, Long productId );
    boolean existsByCart_IdAndProduct_Id(Long cartId , Long productId);
    List<CartItem> findByCart_Id(Long CartId);
    void deleteByCart_Id(Long cartId);
    void deleteByCart_IdAndProduct_Id(Long cartId, Long productId);
}
