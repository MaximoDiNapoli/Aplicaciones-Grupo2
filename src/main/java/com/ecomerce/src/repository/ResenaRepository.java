package com.ecomerce.src.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecomerce.src.entity.Resena;

public interface ResenaRepository extends JpaRepository<Resena, Integer> {

	List<Resena> findByIdProductoOrderByIdDesc(Integer idProducto);
}
