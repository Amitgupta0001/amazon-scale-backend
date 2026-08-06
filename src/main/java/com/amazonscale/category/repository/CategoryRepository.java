package com.amazonscale.category.repository;

import com.amazonscale.category.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    Optional<Category> findByName(String name);

    @Query("SELECT DISTINCT c.name FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY c.name ASC")
    List<String> findTopCategoryNames(@Param("query") String query, Pageable pageable);
}
