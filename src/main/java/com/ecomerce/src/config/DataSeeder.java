package com.ecomerce.src.config;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.imageio.ImageIO;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecomerce.src.entity.Category;
import com.ecomerce.src.entity.Estado;
import com.ecomerce.src.entity.MetodoPago;
import com.ecomerce.src.entity.Product;
import com.ecomerce.src.entity.Resena;
import com.ecomerce.src.entity.User;
import com.ecomerce.src.repository.CategoryRepository;
import com.ecomerce.src.repository.EstadoRepository;
import com.ecomerce.src.repository.MetodoPagoRepository;
import com.ecomerce.src.repository.ProductRepository;
import com.ecomerce.src.repository.ResenaRepository;
import com.ecomerce.src.repository.UserRepository;

// Siembra datos de ejemplo SOLO bajo el perfil "h2" (desarrollo/pruebas en memoria).
// No afecta el perfil por defecto (MySQL, que se puebla con bdd/reset_seed_*.sql).
// Usa el PasswordEncoder real y las MISMAS credenciales que el seed de MySQL:
// admin@gmail.com / vendedor@gmail.com / comprador@gmail.com, contraseña "123".
@Component
@Profile("h2")
public class DataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final EstadoRepository estadoRepository;
	private final MetodoPagoRepository metodoPagoRepository;
	private final ResenaRepository resenaRepository;
	private final PasswordEncoder passwordEncoder;

	public DataSeeder(UserRepository userRepository, CategoryRepository categoryRepository,
			ProductRepository productRepository, EstadoRepository estadoRepository,
			MetodoPagoRepository metodoPagoRepository, ResenaRepository resenaRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;
		this.estadoRepository = estadoRepository;
		this.metodoPagoRepository = metodoPagoRepository;
		this.resenaRepository = resenaRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		if (userRepository.count() > 0) {
			return;
		}

		userRepository.save(newUser("Sofia Molina", "admin@gmail.com", "ADMINISTRADOR"));
		User vendedor = userRepository.save(newUser("Vendedor Demo", "vendedor@gmail.com", "VENDEDOR"));
		User comprador = userRepository.save(newUser("Comprador Demo", "comprador@gmail.com", "COMPRADOR"));

		Category chocolates = categoryRepository.save(new Category("Chocolates", "Tabletas, bombones y trufas"));
		Category gomitas = categoryRepository.save(new Category("Gomitas", "Caramelos de goma y ositos"));

		estadoRepository.save(new Estado("pendiente", "Compra registrada"));
		estadoRepository.save(new Estado("procesando", "En preparación"));
		estadoRepository.save(new Estado("enviado", "En camino"));
		estadoRepository.save(new Estado("entregado", "Recibida por el cliente"));

		metodoPagoRepository.save(new MetodoPago("Tarjeta de crédito", "Visa / Mastercard"));
		metodoPagoRepository.save(new MetodoPago("Efectivo", "Pago al recibir"));

		Product trufa = productRepository.save(newProduct(vendedor.getId(), chocolates.getId(), "Trufa de Cacao Oscuro",
				new BigDecimal("12.50"), 25, "Trufa artesanal de cacao 70% con miel silvestre.", "foto1.png"));
		productRepository.save(newProduct(vendedor.getId(), gomitas.getId(), "Ositos de Goma Ácidos",
				new BigDecimal("4.20"), 120, "Ositos frutales con cobertura ácida.", "foto2.png"));
		productRepository.save(newProduct(vendedor.getId(), chocolates.getId(), "Bombones Rellenos x12",
				new BigDecimal("18.90"), 40, "Caja de 12 bombones rellenos surtidos.", "foto3.jpeg"));
		productRepository.save(newProduct(vendedor.getId(), gomitas.getId(), "Gusanitos Frutales",
				new BigDecimal("3.80"), 200, "Gomitas alargadas con doble sabor.", "foto4.png"));
		productRepository.save(newProduct(vendedor.getId(), chocolates.getId(), "Tableta Chocolate con Leche",
				new BigDecimal("6.50"), 90, "Tableta clásica de chocolate con leche 100g.", "foto1.png"));
		productRepository.save(newProduct(vendedor.getId(), gomitas.getId(), "Malvaviscos de Vainilla",
				new BigDecimal("5.00"), 75, "Nubes esponjosas de vainilla para compartir.", "foto2.png"));

		resenaRepository.save(newResena(trufa.getId(), comprador.getId(), 5, "Excelente, muy recomendado para regalar."));
		resenaRepository.save(newResena(trufa.getId(), comprador.getId(), 4, "Muy rico aunque un poco dulce."));
	}

	private User newUser(String nombre, String email, String rol) {
		User user = new User();
		user.setNombre(nombre);
		user.setEmail(email);
		user.setTelefono("000000000");
		user.setRol(rol);
		user.setPasswordHash(passwordEncoder.encode("123"));
		return user;
	}

	private Product newProduct(Integer usuarioId, Integer categoriaId, String nombre, BigDecimal precio,
			Integer stock, String descripcion, String fotoRecurso) {
		Product product = new Product();
		product.setUsuarioId(usuarioId);
		product.setCategoriaId(categoriaId);
		product.setNombre(nombre);
		product.setPrecio(precio);
		product.setStock(stock);
		product.setDescripcion(descripcion);
		product.setActivo(true);
		product.setFoto(cargarFoto(fotoRecurso));
		return product;
	}

	// Carga una foto real de src/main/resources/seed-images/ y la re-encodea a JPEG
	// (el endpoint /api/productos/{id}/foto sirve image/jpeg). Devuelve null si no se puede leer.
	private byte[] cargarFoto(String recurso) {
		try (InputStream in = new ClassPathResource("seed-images/" + recurso).getInputStream()) {
			BufferedImage original = ImageIO.read(in);
			if (original == null) {
				return null;
			}
			// Aplana posible transparencia (PNG) sobre fondo blanco antes de pasar a JPEG.
			BufferedImage rgb = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
			rgb.createGraphics().drawImage(original, 0, 0, Color.WHITE, null);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(rgb, "jpg", out);
			return out.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

	private Resena newResena(Integer idProducto, Integer idUsuario, int puntuacion, String comentario) {
		Resena resena = new Resena();
		resena.setIdProducto(idProducto);
		resena.setIdUsuario(idUsuario);
		resena.setPuntuacion(puntuacion);
		resena.setComentario(comentario);
		resena.setCreatedAt(java.time.LocalDateTime.now());
		return resena;
	}
}
