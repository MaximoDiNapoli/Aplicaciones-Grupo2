package com.ecomerce.src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.src.entity.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

}
