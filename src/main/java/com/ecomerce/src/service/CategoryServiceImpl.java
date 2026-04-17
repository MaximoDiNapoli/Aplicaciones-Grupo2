package com.ecomerce.src.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.CategoryRequest;
import com.ecomerce.src.entity.Category;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<Category> listar() {
		return categoryRepository.findAll();
	}

	@Override
	public Category obtenerPorId(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con id " + id));
	}

	@Override
	public Category crear(CategoryRequest request) {
		Category category = new Category(request.getNombre(), request.getDescripcion());
		return categoryRepository.save(category);
	}

	@Override
	public Category actualizar(Long id, CategoryRequest request) {
		Category category = obtenerPorId(id);
		category.setNombre(request.getNombre());
		category.setDescripcion(request.getDescripcion());
		return categoryRepository.save(category);
	}

	@Override
	public void eliminar(Long id) {
		Category category = obtenerPorId(id);
		categoryRepository.delete(category);
	}
}