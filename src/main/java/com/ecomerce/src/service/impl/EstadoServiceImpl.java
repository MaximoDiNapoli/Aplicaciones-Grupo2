package com.ecomerce.src.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.EstadoRequest;
import com.ecomerce.src.entity.Estado;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.EstadoRepository;
import com.ecomerce.src.service.EstadoService;

@Service
public class EstadoServiceImpl implements EstadoService {

	private final EstadoRepository estadoRepository;

	public EstadoServiceImpl(EstadoRepository estadoRepository) {
		this.estadoRepository = estadoRepository;
	}

	@Override
	public List<Estado> listar() {
		return estadoRepository.findAll();
	}

	@Override
	public Estado obtenerPorId(Integer id) {
		return estadoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el estado con id " + id));
	}

	@Override
	public Estado crear(EstadoRequest request) {
		Estado estado = new Estado(request.getNombre(), request.getDescripcion());
		return estadoRepository.save(estado);
	}

	@Override
	public Estado actualizar(Integer id, EstadoRequest request) {
		Estado estado = obtenerPorId(id);
		estado.setNombre(request.getNombre());
		estado.setDescripcion(request.getDescripcion());
		return estadoRepository.save(estado);
	}

	@Override
	public void eliminar(Integer id) {
		Estado estado = obtenerPorId(id);
		estado.setNombre("Eliminado");
		estadoRepository.save(estado);
	}
}
