package com.ecomerce.src.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.DireccionEnvioRequest;
import com.ecomerce.src.entity.DireccionEnvio;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.DireccionEnvioRepository;

@Service
public class DireccionEnvioServiceImpl implements DireccionEnvioService {

    private final DireccionEnvioRepository direccionEnvioRepository;

    public DireccionEnvioServiceImpl(DireccionEnvioRepository direccionEnvioRepository) {
        this.direccionEnvioRepository = direccionEnvioRepository;
    }

    @Override
    public List<DireccionEnvio> listar() {
        return direccionEnvioRepository.findAll();
    }

    @Override
    public DireccionEnvio obtenerPorId(Integer id) {
        return direccionEnvioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe dirección con id " + id));
    }

    @Override
    public DireccionEnvio crear(DireccionEnvioRequest request) {
        DireccionEnvio direccion = new DireccionEnvio(
            request.getIdUsuario(),
            request.getDireccion(),
            request.getCiudad(),
            request.getCodigoPostal(),
            request.getEsPrincipal() != null ? request.getEsPrincipal() : false
        );
        return direccionEnvioRepository.save(direccion);
    }

    @Override
    public DireccionEnvio actualizar(Integer id, DireccionEnvioRequest request) {
        DireccionEnvio direccion = direccionEnvioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe dirección con id " + id));

        direccion.setIdUsuario(request.getIdUsuario());
        direccion.setDireccion(request.getDireccion());
        direccion.setCiudad(request.getCiudad());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setEsPrincipal(request.getEsPrincipal() != null ? request.getEsPrincipal() : false);

        return direccionEnvioRepository.save(direccion);
    }

    @Override
    public void eliminar(Integer id) {
        DireccionEnvio direccion = direccionEnvioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe dirección con id " + id));
        direccionEnvioRepository.delete(direccion);
    }
}

