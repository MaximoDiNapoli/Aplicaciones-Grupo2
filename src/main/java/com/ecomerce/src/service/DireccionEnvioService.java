package com.ecomerce.src.service;

import java.util.List;
import com.ecomerce.src.dto.DireccionEnvioRequest;
import com.ecomerce.src.entity.DireccionEnvio;

public interface DireccionEnvioService {

    List<DireccionEnvio> listar();

    DireccionEnvio obtenerPorId(Integer id);

    DireccionEnvio crear(DireccionEnvioRequest request);

    DireccionEnvio actualizar(Integer id, DireccionEnvioRequest request);

    void eliminar(Integer id);
}

