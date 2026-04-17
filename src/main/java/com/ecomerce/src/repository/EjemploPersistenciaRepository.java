package com.ecomerce.src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.EjemploPersistencia;

@Repository
public interface EjemploPersistenciaRepository extends JpaRepository<EjemploPersistencia, Long> {
}