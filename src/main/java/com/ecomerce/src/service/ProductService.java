package com.ecomerce.src.service;

import java.math.BigDecimal;
import java.util.List;

import com.ecomerce.src.dto.ProductRequest;
import com.ecomerce.src.entity.Product;

public interface ProductService {

	List<Product> listar(Long categoria, String search, BigDecimal minPrecio, BigDecimal maxPrecio);

	Product obtenerPorId(Long id);

	Product crear(ProductRequest request);

	Product actualizar(Long id, ProductRequest request);

	void eliminarLogico(Long id);
}