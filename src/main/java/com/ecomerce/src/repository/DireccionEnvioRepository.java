package com.ecomerce.src.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.DireccionEnvio;

@Repository
public interface DireccionEnvioRepository extends JpaRepository<DireccionEnvio, Integer> {

	List<DireccionEnvio> findByIdUsuario(Integer idUsuario);
}

