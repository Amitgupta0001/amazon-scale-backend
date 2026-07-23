package com.amazonscale.user.repository;

import com.amazonscale.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email); // Returns Optional to explicitly represent that a user may or may not exist.
    boolean existsByEmail(String email);
}
