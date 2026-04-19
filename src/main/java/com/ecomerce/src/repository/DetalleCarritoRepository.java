package com.ecomerce.src.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.DetalleCarrito;

@Repository
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Integer> {

	List<DetalleCarrito> findByCarritoId(Integer carritoId);

	Optional<DetalleCarrito> findByIdAndCarritoId(Integer id, Integer carritoId);

	void deleteByCarritoId(Integer carritoId);
}
