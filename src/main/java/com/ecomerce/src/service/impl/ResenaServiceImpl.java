package com.ecomerce.src.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.ResenaRequest;
import com.ecomerce.src.dto.ResenaResponse;
import com.ecomerce.src.entity.Resena;
import com.ecomerce.src.entity.User;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.ProductRepository;
import com.ecomerce.src.repository.ResenaRepository;
import com.ecomerce.src.repository.UserRepository;
import com.ecomerce.src.security.CurrentUserService;
import com.ecomerce.src.service.ResenaService;

@Service
public class ResenaServiceImpl implements ResenaService {

	private final ResenaRepository resenaRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final CurrentUserService currentUserService;

	public ResenaServiceImpl(ResenaRepository resenaRepository, UserRepository userRepository,
			ProductRepository productRepository, CurrentUserService currentUserService) {
		this.resenaRepository = resenaRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
		this.currentUserService = currentUserService;
	}

	@Override
	public List<ResenaResponse> listarPorProducto(Integer idProducto) {
		List<Resena> resenas = resenaRepository.findByIdProductoOrderByIdDesc(idProducto);
		List<Integer> autorIds = resenas.stream().map(Resena::getIdUsuario).distinct().toList();
		Map<Integer, String> nombres = userRepository.findAllById(autorIds).stream()
				.collect(Collectors.toMap(User::getId, User::getNombre));
		return resenas.stream()
				.map(r -> ResenaResponse.from(r, nombres.getOrDefault(r.getIdUsuario(), "Usuario")))
				.collect(Collectors.toList());
	}

	@Override
	public ResenaResponse crear(ResenaRequest request) {
		productRepository.findById(request.getIdProducto())
				.orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + request.getIdProducto()));

		Integer userId = currentUserService.getCurrentUserId();
		Resena resena = new Resena();
		resena.setIdProducto(request.getIdProducto());
		resena.setIdUsuario(userId);
		resena.setPuntuacion(request.getPuntuacion());
		resena.setComentario(request.getComentario());
		resena.setCreatedAt(LocalDateTime.now());
		Resena guardada = resenaRepository.save(resena);

		String autor = userRepository.findById(userId).map(User::getNombre).orElse("Usuario");
		return ResenaResponse.from(guardada, autor);
	}
}
