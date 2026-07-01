package com.ecomerce.src.service;

import java.util.List;

import com.ecomerce.src.dto.ResenaRequest;
import com.ecomerce.src.dto.ResenaResponse;

public interface ResenaService {

	List<ResenaResponse> listarPorProducto(Integer idProducto);

	ResenaResponse crear(ResenaRequest request);
}
