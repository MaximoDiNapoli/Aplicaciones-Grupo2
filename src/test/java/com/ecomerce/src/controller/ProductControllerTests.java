package com.ecomerce.src.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ecomerce.src.entity.Product;
import com.ecomerce.src.repository.ProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTests {

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${local.server.port}")
	private int port;

	@BeforeEach
	void cleanDatabase() {
		productRepository.deleteAll();
		jdbcTemplate.update("DELETE FROM Categoria WHERE id IN (1, 2)");
		jdbcTemplate.update("INSERT INTO Categoria (id, nombre, descripcion) VALUES (1, 'Cat 1', 'Categoria de pruebas 1')");
		jdbcTemplate.update("INSERT INTO Categoria (id, nombre, descripcion) VALUES (2, 'Cat 2', 'Categoria de pruebas 2')");
		jdbcTemplate.update("DELETE FROM Usuario WHERE id = 9001");
		jdbcTemplate.update(
				"INSERT INTO Usuario (id, nombre, email, password_hash, rol) VALUES (9001, 'Test User', 'test.user@example.com', 'hash', 'ADMIN')");
	}

	@Test
	void shouldCreateAndGetProductById() throws Exception {
		HttpRequest createRequest = HttpRequest.newBuilder(uri("/api/productos"))
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.POST(HttpRequest.BodyPublishers.ofString("""
					{
					  "usuarioId": 9001,
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

		Integer id = extractId(createResponse.body());
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
		productRepository.save(buildProduct(1, "Teclado Gamer", "Mecanico", new BigDecimal("80.00"), 7));
		productRepository.save(buildProduct(1, "Mouse Gamer", "Optico", new BigDecimal("20.00"), 15));
		productRepository.save(buildProduct(2, "Silla Oficina", "Ergonomica", new BigDecimal("150.00"), 3));

		HttpResponse<String> byCategory = httpClient.send(
				HttpRequest.newBuilder(uri("/api/productos?categoria=1")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, byCategory.statusCode());
		assertEquals(2, countArrayItems(byCategory.body()));

		HttpResponse<String> byUser = httpClient.send(
				HttpRequest.newBuilder(uri("/api/productos?usuario=9001")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, byUser.statusCode());
		assertEquals(3, countArrayItems(byUser.body()));

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
		Product product = buildProduct(1, "Monitor 24", "IPS", new BigDecimal("120.00"), 4);
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

	private Product buildProduct(Integer categoriaId, String nombre, String descripcion, BigDecimal precio, int stock) {
		Product product = new Product();
		product.setUsuarioId(9001);
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

	private Integer extractId(String json) {
		Pattern pattern = Pattern.compile("\\\"id\\\"\\s*:\\s*(\\d+)");
		Matcher matcher = pattern.matcher(json);
		if (!matcher.find()) {
			return null;
		}
		return Integer.parseInt(matcher.group(1));
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
