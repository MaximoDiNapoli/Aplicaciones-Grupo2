package com.ecomerce.src.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {

	List<Compra> findByIdUsuario(Integer idUsuario);
}
