package com.ecomerce.src.repository;

import com.ecomerce.src.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByRol(String rol);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
