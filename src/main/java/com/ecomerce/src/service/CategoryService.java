package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.CategoryRequest;
import com.ecomerce.src.entity.Category;

public interface CategoryService {

	List<Category> listar();

	Category obtenerPorId(Integer id);

	Category crear(CategoryRequest request);

	Category actualizar(Integer id, CategoryRequest request);

	void eliminar(Integer id);
}