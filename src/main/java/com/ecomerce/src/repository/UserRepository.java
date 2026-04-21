package com.ecomerce.src.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByRol(String rol);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
