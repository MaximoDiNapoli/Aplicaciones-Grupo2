package com.ecomerce.src.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ecomerce.src.entity.Product;
import com.ecomerce.src.repository.ProductRepository;

class ProductControllerTests {

	private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
	private static ConfigurableApplicationContext context;
	private static ProductRepository productRepository;
	private static JdbcTemplate jdbcTemplate;
	private static final int PORT = 18080;

	@BeforeAll
	static void startApplication() {
		System.setProperty("server.port", String.valueOf(PORT));
		System.setProperty("spring.datasource.url", "jdbc:h2:mem:ecomerce_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
		System.setProperty("spring.datasource.username", "sa");
		System.setProperty("spring.datasource.password", "");
		System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
		System.setProperty("spring.jpa.database-platform", "org.hibernate.dialect.H2Dialect");
		System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
		context = new SpringApplicationBuilder(TestApplication.class).run();
		productRepository = context.getBean(ProductRepository.class);
		jdbcTemplate = context.getBean(JdbcTemplate.class);
	}

	@AfterAll
	static void stopApplication() {
		if (context != null) {
			context.close();
		}
	}

	@BeforeEach
	void cleanDatabase() {
		productRepository.deleteAll();
		jdbcTemplate.update("DELETE FROM categoria WHERE id IN (1, 2)");
		jdbcTemplate.update("INSERT INTO categoria (id, nombre, descripcion) VALUES (1, 'Cat 1', 'Categoria de pruebas 1')");
		jdbcTemplate.update("INSERT INTO categoria (id, nombre, descripcion) VALUES (2, 'Cat 2', 'Categoria de pruebas 2')");
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
					  "stock": 10
					}
					"""))
				.build();

		HttpResponse<String> createResponse = HTTP_CLIENT.send(createRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(201, createResponse.statusCode());

		Integer id = extractId(createResponse.body());
		assertNotNull(id);

		HttpResponse<String> getResponse = HTTP_CLIENT.send(
				HttpRequest.newBuilder(uri("/api/productos/" + id)).GET().build(),
				HttpResponse.BodyHandlers.ofString());

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

		HttpResponse<String> byCategory = HTTP_CLIENT.send(
				HttpRequest.newBuilder(uri("/api/productos?categoria=1")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, byCategory.statusCode());
		assertEquals(2, countArrayItems(byCategory.body()));

		HttpResponse<String> byUser = HTTP_CLIENT.send(
				HttpRequest.newBuilder(uri("/api/productos?usuario=9001")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, byUser.statusCode());
		assertEquals(3, countArrayItems(byUser.body()));

		HttpResponse<String> bySearch = HTTP_CLIENT.send(
				HttpRequest.newBuilder(uri("/api/productos?search=teclado")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, bySearch.statusCode());
		assertEquals(1, countArrayItems(bySearch.body()));

		HttpResponse<String> byPrice = HTTP_CLIENT.send(
				HttpRequest.newBuilder(uri("/api/productos?minPrecio=10&maxPrecio=100")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, byPrice.statusCode());
		assertEquals(2, countArrayItems(byPrice.body()));
	}

	@Test
	void shouldLogicalDeleteProduct() throws Exception {
		Product product = productRepository.save(buildProduct(1, "Monitor 24", "IPS", new BigDecimal("120.00"), 4));

		HttpResponse<String> deleteResponse = HTTP_CLIENT.send(
				HttpRequest.newBuilder(uri("/api/productos/" + product.getId())).DELETE().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(204, deleteResponse.statusCode());

		HttpResponse<String> getDeletedResponse = HTTP_CLIENT.send(
				HttpRequest.newBuilder(uri("/api/productos/" + product.getId())).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(404, getDeletedResponse.statusCode());

		Product dbProduct = productRepository.findById(product.getId()).orElseThrow();
		assertNotNull(dbProduct.getActivo());
		assertEquals(false, dbProduct.getActivo());
	}

	@Test
	void shouldHandlePhotoOnCreateAndUpdateMultipart() throws Exception {
		byte[] createdPhotoBytes = "photo-bytes-create".getBytes(StandardCharsets.UTF_8);
		byte[] updatedPhotoBytes = "photo-bytes-update".getBytes(StandardCharsets.UTF_8);
		String boundary = "----BoundaryForProductPhotoTest";

		HttpRequest createRequest = HttpRequest.newBuilder(uri("/api/productos"))
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.POST(HttpRequest.BodyPublishers.ofString(buildMultipartProductBody(
						boundary,
						"9001",
						"1",
						"Camara Web",
						"35.00",
						"HD",
						"8",
						createdPhotoBytes), StandardCharsets.UTF_8))
				.build();

		HttpResponse<String> createResponse = HTTP_CLIENT.send(createRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(201, createResponse.statusCode());
		Integer id = extractId(createResponse.body());
		assertNotNull(id);

		Product createdProduct = productRepository.findById(id).orElseThrow();
		assertArrayEquals(createdPhotoBytes, createdProduct.getFoto());

		HttpRequest updateRequest = HttpRequest.newBuilder(uri("/api/productos/" + id))
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.PUT(HttpRequest.BodyPublishers.ofString(buildMultipartProductBody(
						boundary,
						"9001",
						"2",
						"Camara Web Editada",
						"38.50",
						"Full HD",
						"10",
						updatedPhotoBytes), StandardCharsets.UTF_8))
				.build();

		HttpResponse<String> updateResponse = HTTP_CLIENT.send(updateRequest, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, updateResponse.statusCode());

		Product updatedProduct = productRepository.findById(id).orElseThrow();
		assertArrayEquals(updatedPhotoBytes, updatedProduct.getFoto());
		assertEquals("Camara Web Editada", updatedProduct.getNombre());
		assertEquals(2, updatedProduct.getCategoriaId());
	}

	private Product buildProduct(Integer categoriaId, String nombre, String descripcion, BigDecimal precio, int stock) {
		Product product = new Product();
		product.setUsuarioId(9001);
		product.setCategoriaId(categoriaId);
		product.setNombre(nombre);
		product.setDescripcion(descripcion);
		product.setPrecio(precio);
		product.setStock(stock);
		product.setActivo(true);
		return product;
	}

	private URI uri(String path) {
		return URI.create("http://localhost:" + PORT + path);
	}

	private Integer extractId(String json) {
		Pattern pattern = Pattern.compile("\\\"id\\\"\\s*:\\s*(\\d+)");
		Matcher matcher = pattern.matcher(json);
		if (!matcher.find()) {
			return null;
		}
		return Integer.valueOf(matcher.group(1));
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

	private String buildMultipartProductBody(
			String boundary,
			String usuarioId,
			String categoriaId,
			String nombre,
			String precio,
			String descripcion,
			String stock,
			byte[] fileBytes) {
		String lineBreak = "\r\n";
		String fields = "--" + boundary + lineBreak
				+ "Content-Disposition: form-data; name=\"usuarioId\"" + lineBreak + lineBreak + usuarioId + lineBreak
				+ "--" + boundary + lineBreak
				+ "Content-Disposition: form-data; name=\"categoriaId\"" + lineBreak + lineBreak + categoriaId + lineBreak
				+ "--" + boundary + lineBreak
				+ "Content-Disposition: form-data; name=\"nombre\"" + lineBreak + lineBreak + nombre + lineBreak
				+ "--" + boundary + lineBreak
				+ "Content-Disposition: form-data; name=\"precio\"" + lineBreak + lineBreak + precio + lineBreak
				+ "--" + boundary + lineBreak
				+ "Content-Disposition: form-data; name=\"descripcion\"" + lineBreak + lineBreak + descripcion + lineBreak
				+ "--" + boundary + lineBreak
				+ "Content-Disposition: form-data; name=\"stock\"" + lineBreak + lineBreak + stock + lineBreak;

		String fileHeader = "--" + boundary + lineBreak
				+ "Content-Disposition: form-data; name=\"image\"; filename=\"photo.txt\"" + lineBreak
				+ "Content-Type: application/octet-stream" + lineBreak + lineBreak;
		String footer = lineBreak + "--" + boundary + "--" + lineBreak;
		byte[] fieldsBytes = fields.getBytes(StandardCharsets.UTF_8);
		byte[] fileHeaderBytes = fileHeader.getBytes(StandardCharsets.UTF_8);
		byte[] footerBytes = footer.getBytes(StandardCharsets.UTF_8);
		byte[] multipartBody = new byte[fieldsBytes.length + fileHeaderBytes.length + fileBytes.length + footerBytes.length];
		System.arraycopy(fieldsBytes, 0, multipartBody, 0, fieldsBytes.length);
		System.arraycopy(fileHeaderBytes, 0, multipartBody, fieldsBytes.length, fileHeaderBytes.length);
		System.arraycopy(fileBytes, 0, multipartBody, fieldsBytes.length + fileHeaderBytes.length, fileBytes.length);
		System.arraycopy(footerBytes, 0, multipartBody, fieldsBytes.length + fileHeaderBytes.length + fileBytes.length, footerBytes.length);
		return new String(multipartBody, StandardCharsets.UTF_8);
	}

	@SpringBootApplication(scanBasePackages = "com.ecomerce.src")
	@EnableJpaRepositories(basePackages = "com.ecomerce.src.repository")
	static class TestApplication {
	}
}
