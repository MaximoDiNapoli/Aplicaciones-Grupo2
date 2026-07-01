package com.ecomerce.src.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.DetalleCompra;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Integer> {

	List<DetalleCompra> findByIdCompra(Integer idCompra);

	// True si la compra incluye al menos un producto del vendedor indicado.
	boolean existsByIdCompraAndProducto_UsuarioId(Integer idCompra, Integer usuarioId);

	void deleteByIdCompra(Integer idCompra);
}
