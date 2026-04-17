package com.ecomerce.src.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;

import com.ecomerce.src.entity.Category;
import com.ecomerce.src.repository.CategoryRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerIT {

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	@Value("${local.server.port}")
	private int port;

	@BeforeEach
	void cleanDatabase() {
		categoryRepository.deleteAll();
	}

	@Test
	void shouldCreateCategory() throws Exception {
		HttpRequest request = HttpRequest.newBuilder(uri("/api/categorias"))
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.POST(HttpRequest.BodyPublishers.ofString("""
					{
					  "nombre": "Tecnologia",
					  "descripcion": "Productos tech"
					}
					"""))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(HttpStatus.CREATED.value(), response.statusCode());
		assertNotNull(response.body());
		assertEquals(true, response.body().contains("Tecnologia"));
		assertEquals(true, response.body().contains("Productos tech"));
		assertEquals(1, categoryRepository.count());
	}

	@Test
	void shouldListCategories() throws Exception {
		Category categoria = categoryRepository.save(new Category("Hogar", "Productos para hogar"));

		HttpRequest request = HttpRequest.newBuilder(uri("/api/categorias")).GET().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(HttpStatus.OK.value(), response.statusCode());
		assertNotNull(response.body());
		assertEquals(true, response.body().contains("Hogar"));
		assertEquals(true, response.body().contains("Productos para hogar"));
		assertEquals(1, categoryRepository.count());
		assertEquals(categoria.getId(), categoryRepository.findAll().get(0).getId());
	}

	@Test
	void shouldGetCategoryById() throws Exception {
		Category categoria = categoryRepository.save(new Category("Libros", "Categoria de lectura"));

		HttpRequest request = HttpRequest.newBuilder(uri("/api/categorias/" + categoria.getId())).GET().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(HttpStatus.OK.value(), response.statusCode());
		assertNotNull(response.body());
		assertEquals(true, response.body().contains("Libros"));
		assertEquals(true, response.body().contains("Categoria de lectura"));
	}

	@Test
	void shouldUpdateCategory() throws Exception {
		Category categoria = categoryRepository.save(new Category("Ropa", "Categoria inicial"));

		HttpRequest request = HttpRequest.newBuilder(uri("/api/categorias/" + categoria.getId()))
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.PUT(HttpRequest.BodyPublishers.ofString("""
					{
					  "nombre": "Ropa deportiva",
					  "descripcion": "Categoria actualizada"
					}
					"""))
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(HttpStatus.OK.value(), response.statusCode());
		assertNotNull(response.body());
		assertEquals(true, response.body().contains("Ropa deportiva"));
		assertEquals(true, response.body().contains("Categoria actualizada"));

		Category updated = categoryRepository.findById(categoria.getId()).orElseThrow();
		assertEquals("Ropa deportiva", updated.getNombre());
		assertEquals("Categoria actualizada", updated.getDescripcion());
	}

	@Test
	void shouldDeleteCategory() throws Exception {
		Category categoria = categoryRepository.save(new Category("Juguetes", "Categoria temporal"));

		HttpRequest deleteRequest = HttpRequest.newBuilder(uri("/api/categorias/" + categoria.getId()))
				.DELETE()
				.build();
		HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.statusCode());
		assertEquals(0, categoryRepository.count());

		HttpRequest getRequest = HttpRequest.newBuilder(uri("/api/categorias/" + categoria.getId())).GET().build();
		HttpResponse<String> notFound = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

		assertEquals(HttpStatus.NOT_FOUND.value(), notFound.statusCode());
		assertNotNull(notFound.body());
		assertEquals(true, notFound.body().contains("No existe la categoria con id " + categoria.getId()));
	}

	private URI uri(String path) {
		return URI.create("http://localhost:" + port + path);
	}
}