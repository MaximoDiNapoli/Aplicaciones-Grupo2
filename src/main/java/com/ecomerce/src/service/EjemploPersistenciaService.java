package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.EjemploPersistenciaRequest;
import com.ecomerce.src.entity.EjemploPersistencia;

public interface EjemploPersistenciaService {

	EjemploPersistencia crear(EjemploPersistenciaRequest request);

	List<EjemploPersistencia> listar();

	EjemploPersistencia buscarPorId(Long id);
}