package com.ecomerce.src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
