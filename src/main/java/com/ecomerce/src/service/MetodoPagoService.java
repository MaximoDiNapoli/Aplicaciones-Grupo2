package com.ecomerce.src.service;

import java.util.List;
import com.ecomerce.src.dto.MetodoPagoRequest;
import com.ecomerce.src.entity.MetodoPago;

public interface MetodoPagoService {

    List<MetodoPago> listar();

    MetodoPago obtenerPorId(Integer id);

    MetodoPago crear(MetodoPagoRequest request);

    MetodoPago actualizar(Integer id, MetodoPagoRequest request);

    void eliminar(Integer id);
}