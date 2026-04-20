package com.ecomerce.src.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

	List<Carrito> findByUsuarioId(Integer usuarioId);

}
