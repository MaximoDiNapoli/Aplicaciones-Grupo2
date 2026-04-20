package com.ecomerce.src.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecomerce.src.entity.AuthUser;

public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {

	Optional<AuthUser> findByEmail(String email);

	boolean existsByEmail(String email);
}
