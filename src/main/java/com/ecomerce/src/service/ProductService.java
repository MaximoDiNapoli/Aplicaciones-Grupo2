package com.ecomerce.src.service;

import java.math.BigDecimal;
import java.util.List;

import com.ecomerce.src.dto.ProductRequest;
import com.ecomerce.src.entity.Product;

public interface ProductService {

	List<Product> listar(Integer usuario, Integer categoria, String search, BigDecimal minPrecio, BigDecimal maxPrecio);

	Product obtenerPorId(Integer id);

	Product crear(ProductRequest request);

	Product actualizar(Integer id, ProductRequest request);

	void eliminarLogico(Integer id);
}