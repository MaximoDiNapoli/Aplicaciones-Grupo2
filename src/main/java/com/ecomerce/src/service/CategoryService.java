package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.CategoryRequest;
import com.ecomerce.src.entity.Category;

public interface CategoryService {

	List<Category> listar();

	Category obtenerPorId(Long id);

	Category crear(CategoryRequest request);

	Category actualizar(Long id, CategoryRequest request);

	void eliminar(Long id);
}