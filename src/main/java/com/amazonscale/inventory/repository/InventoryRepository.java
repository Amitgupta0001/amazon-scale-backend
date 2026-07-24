package com.amazonscale.inventory.repository;

import com.amazonscale.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {


    Optional<Inventory> findByProductId(Long id);
    boolean existsByProductId(Long id);
}
