package com.ecomerce.src.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.EjemploPersistenciaRequest;
import com.ecomerce.src.entity.EjemploPersistencia;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.EjemploPersistenciaRepository;

@Service
public class EjemploPersistenciaServiceImpl implements EjemploPersistenciaService {

	private final EjemploPersistenciaRepository repository;

	public EjemploPersistenciaServiceImpl(EjemploPersistenciaRepository repository) {
		this.repository = repository;
	}

	@Override
	public EjemploPersistencia crear(EjemploPersistenciaRequest request) {
		EjemploPersistencia entity = new EjemploPersistencia(request.getNombre(), request.getDescripcion());
		return repository.save(entity);
	}

	@Override
	public List<EjemploPersistencia> listar() {
		return repository.findAll();
	}

	@Override
	public EjemploPersistencia buscarPorId(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe un ejemplo de persistencia con id " + id));
	}
}