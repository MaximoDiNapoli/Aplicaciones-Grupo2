package com.ecomerce.src.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {

	List<Compra> findByIdUsuario(Integer idUsuario);

	// Compras que incluyen al menos un producto del vendedor indicado.
	@Query("SELECT DISTINCT d.compra FROM DetalleCompra d WHERE d.producto.usuarioId = :vendedorId")
	List<Compra> findDistinctByVendedor(@Param("vendedorId") Integer vendedorId);
}
