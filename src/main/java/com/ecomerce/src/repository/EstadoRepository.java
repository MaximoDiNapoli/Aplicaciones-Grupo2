package com.ecomerce.src.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {

	Optional<Estado> findFirstByNombreIgnoreCase(String nombre);
}
