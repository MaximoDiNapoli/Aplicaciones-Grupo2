package com.ecomerce.src.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.MetodoPagoRequest;
import com.ecomerce.src.entity.MetodoPago;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.MetodoPagoRepository;

@Service
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    public MetodoPagoServiceImpl(MetodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @Override
    public List<MetodoPago> listar() {
        return metodoPagoRepository.findAll();
    }

    @Override
    public MetodoPago obtenerPorId(Integer id) {
        return metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el método de pago con id " + id));
    }

    @Override
    public MetodoPago crear(MetodoPagoRequest request) {
        // Asumiendo que MetodoPago tiene un constructor similar al de Carrito
        MetodoPago metodoPago = new MetodoPago(request.getTipo(), request.getDescripcion());
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public MetodoPago actualizar(Integer id, MetodoPagoRequest request) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el método de pago con id " + id));

        metodoPago.setTipo(request.getTipo());
        metodoPago.setDescripcion(request.getDescripcion());

        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public void eliminar(Integer id) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el método de pago con id " + id));
        metodoPagoRepository.delete(metodoPago);
    }
}