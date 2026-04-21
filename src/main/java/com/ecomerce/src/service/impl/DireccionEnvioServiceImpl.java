package com.ecomerce.src.service.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.DireccionEnvioRequest;
import com.ecomerce.src.entity.DireccionEnvio;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.DireccionEnvioRepository;
import com.ecomerce.src.security.CurrentUserService;
import com.ecomerce.src.service.DireccionEnvioService;

@Service
public class DireccionEnvioServiceImpl implements DireccionEnvioService {

    private final DireccionEnvioRepository direccionEnvioRepository;
    private final CurrentUserService currentUserService;

    public DireccionEnvioServiceImpl(DireccionEnvioRepository direccionEnvioRepository, CurrentUserService currentUserService) {
        this.direccionEnvioRepository = direccionEnvioRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public List<DireccionEnvio> listar() {
        Integer currentUserId = currentUserService.getCurrentUserId();
        return direccionEnvioRepository.findByIdUsuario(currentUserId);
    }

    @Override
    public List<DireccionEnvio> listarPorUsuario(Integer idUsuario) {
        return direccionEnvioRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public DireccionEnvio obtenerPorId(Integer id) {
        DireccionEnvio direccion = direccionEnvioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe dirección con id " + id));
        validateOwnership(direccion);
        return direccion;
    }

    @Override
    public DireccionEnvio crear(DireccionEnvioRequest request) {
        Integer currentUserId = currentUserService.getCurrentUserId();
        Boolean esPrincipal = request.getEsPrincipal() != null ? request.getEsPrincipal() : Boolean.FALSE;
        DireccionEnvio direccion = new DireccionEnvio(
            currentUserId,
            request.getDireccion(),
            request.getCiudad(),
            request.getCodigoPostal(),
            esPrincipal
        );
        return direccionEnvioRepository.save(direccion);
    }

    @Override
    public DireccionEnvio actualizar(Integer id, DireccionEnvioRequest request) {
        DireccionEnvio direccion = obtenerPorId(id);
        direccion.setDireccion(request.getDireccion());
        direccion.setCiudad(request.getCiudad());
        direccion.setCodigoPostal(request.getCodigoPostal());
        Boolean esPrincipal = request.getEsPrincipal() != null ? request.getEsPrincipal() : Boolean.FALSE;
        direccion.setEsPrincipal(esPrincipal);

        return direccionEnvioRepository.save(direccion);
    }

    @Override
    public void eliminar(Integer id) {
        DireccionEnvio direccion = obtenerPorId(id);
        direccionEnvioRepository.delete(direccion);
    }

    private void validateOwnership(DireccionEnvio direccion) {
        if (currentUserService.isAdmin()) {
            return;
        }

        Integer currentUserId = currentUserService.getCurrentUserId();
        if (!currentUserId.equals(direccion.getIdUsuario())) {
            throw new AccessDeniedException("No tiene permisos para acceder a esta dirección");
        }
    }
}