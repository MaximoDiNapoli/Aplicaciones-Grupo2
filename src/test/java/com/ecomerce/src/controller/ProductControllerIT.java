package com.ecomerce.src.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.ecomerce.src.entity.Product;
import com.ecomerce.src.repository.ProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIT {

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	@Value("${local.server.port}")
	private int port;

	@BeforeEach
	void cleanDatabase() {
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateAndGetProductById() throws Exception {
		HttpRequest createRequest = HttpRequest.newBuilder(uri("/api/productos"))
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.POST(HttpRequest.BodyPublishers.ofString("""
					{
					  "categoriaId": 1,
					  "nombre": "Teclado Mecanico",
					  "precio": 50.00,
					  "descripcion": "Switch blue",
					  "stock": 10,
					  "imagenUrl": "https://img.test/teclado.png"
					}
					"""))
				.build();

		HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(201, createResponse.statusCode());

		Long id = extractId(createResponse.body());
		assertNotNull(id);

		HttpRequest getRequest = HttpRequest.newBuilder(uri("/api/productos/" + id)).GET().build();
		HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

		assertEquals(200, getResponse.statusCode());
		assertEquals(true, getResponse.body().contains("Teclado Mecanico"));
		assertEquals(true, getResponse.body().contains("Switch blue"));
		assertEquals(1, productRepository.count());
	}

	@Test
	void shouldFilterProductsWithSingleEndpoint() throws Exception {
		productRepository.save(buildProduct(1L, "Teclado Gamer", "Mecanico", new BigDecimal("80.00"), 7));
		productRepository.save(buildProduct(1L, "Mouse Gamer", "Optico", new BigDecimal("20.00"), 15));
		productRepository.save(buildProduct(2L, "Silla Oficina", "Ergonomica", new BigDecimal("150.00"), 3));

		HttpResponse<String> byCategory = httpClient.send(
				HttpRequest.newBuilder(uri("/api/productos?categoria=1")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, byCategory.statusCode());
		assertEquals(2, countArrayItems(byCategory.body()));

		HttpResponse<String> bySearch = httpClient.send(
				HttpRequest.newBuilder(uri("/api/productos?search=teclado")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, bySearch.statusCode());
		assertEquals(1, countArrayItems(bySearch.body()));

		HttpResponse<String> byPrice = httpClient.send(
				HttpRequest.newBuilder(uri("/api/productos?minPrecio=10&maxPrecio=100")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, byPrice.statusCode());
		assertEquals(2, countArrayItems(byPrice.body()));
	}

	@Test
	void shouldLogicalDeleteProduct() throws Exception {
		Product product = buildProduct(1L, "Monitor 24", "IPS", new BigDecimal("120.00"), 4);
		product = productRepository.save(product);

		HttpResponse<String> deleteResponse = httpClient.send(
				HttpRequest.newBuilder(uri("/api/productos/" + product.getId())).DELETE().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(204, deleteResponse.statusCode());

		HttpResponse<String> getDeletedResponse = httpClient.send(
				HttpRequest.newBuilder(uri("/api/productos/" + product.getId())).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(404, getDeletedResponse.statusCode());

		Product dbProduct = productRepository.findById(product.getId()).orElseThrow();
		assertNotNull(dbProduct.getActivo());
		assertEquals(false, dbProduct.getActivo());
	}

	private Product buildProduct(Long categoriaId, String nombre, String descripcion, BigDecimal precio, int stock) {
		Product product = new Product();
		product.setCategoriaId(categoriaId);
		product.setNombre(nombre);
		product.setDescripcion(descripcion);
		product.setPrecio(precio);
		product.setStock(stock);
		product.setImagenUrl("https://img.test/item.png");
		product.setActivo(true);
		return product;
	}

	private URI uri(String path) {
		return URI.create("http://localhost:" + port + path);
	}

	private Long extractId(String json) {
		Pattern pattern = Pattern.compile("\\\"id\\\"\\s*:\\s*(\\d+)");
		Matcher matcher = pattern.matcher(json);
		if (!matcher.find()) {
			return null;
		}
		return Long.parseLong(matcher.group(1));
	}

	private int countArrayItems(String jsonArray) {
		String trimmed = jsonArray == null ? "" : jsonArray.trim();
		if (trimmed.equals("[]")) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < trimmed.length(); i++) {
			if (trimmed.charAt(i) == '{') {
				count++;
			}
		}
		return count;
	}
}