package com.titta.api.features.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.titta.api.domain.model.Categoria;
import com.titta.api.domain.model.Sede;
import com.titta.api.domain.repository.CategoriaRepository;
import com.titta.api.domain.repository.SedeRepository;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.domain.model.Direccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

/**
 * Clase de prueba de integración para ProductoController.
 * Verifica el flujo completo de la petición HTTP, pasando por la capa de
 * seguridad,
 * servicio y persistencia en base de datos.
 */
@SpringBootTest // Carga el contexto completo de la aplicación Spring Boot.
@AutoConfigureMockMvc // Configura automáticamente la instancia de MockMvc para simular peticiones
                      // HTTP.
@ActiveProfiles("test") // Activa el perfil "test" para cargar configuraciones específicas (ej.
                        // application-test.properties).
@Transactional // Anotación que indica que cada método de prueba se ejecutará en una transacción
                // que se deshace al finalizar el método. Esto asegura que la base de datos se
                // restablezca al estado inicial entre pruebas.
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Herramienta principal para realizar peticiones HTTP simuladas al controlador.

    @Autowired
    private ObjectMapper objectMapper; // Utilidad para serializar objetos Java a JSON y viceversa.

    @Autowired
    private CategoriaRepository categoriaRepository; // Repositorio para gestionar datos de prueba de Categorías.

    @Autowired
    private SedeRepository sedeRepository; // Repositorio para gestionar datos de prueba de Sedes.

    /**
     * Método que se ejecuta antes de cada test (@BeforeEach).
     * Se encarga de preparar el estado inicial de la base de datos (seeding),
     * asegurando que existan las entidades necesarias (Categoria y Sede) para crear
     * un Producto.
     */
    @BeforeEach
    void setup() {
        // Crear y guardar una Categoría de prueba
        Categoria categoria = new Categoria();
        categoria.setNombreCategoria("Electrónicos");
        categoria.setEstado(true);
        categoriaRepository.save(categoria);

        // Crear y guardar una Sede con su Dirección
        Sede sede = new Sede();
        sede.setNombreSede("Sede Central");

        Direccion direccion = new Direccion();
        direccion.setCalle("Av. Test 123");
        direccion.setCiudad("Lima");
        direccion.setEstadoProvincial("Lima");
        sede.setDireccion(direccion);

        sede.setTelefono("555-5555");
        sede.setEstado(true);

        sedeRepository.save(sede);
    }

    /**
     * Test de integración para el caso de éxito en la creación de un producto.
     * Escenario: Usuario con rol ADMINISTRADOR y permiso GESTIONAR_PRODUCTOS.
     * Resultado esperado: Código HTTP 201 Created y JSON con los datos del producto
     * creado.
     */
    @Test
    @DisplayName("POST /api/v1/productos - Debería crear un producto y devolver 201 Created")
    // Simulamos un usuario autenticado con los roles y autoridades necesarios
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMINISTRADOR", "GESTIONAR_PRODUCTOS" })
    void crearProducto_IntegrationTest() throws Exception {
        // 1. GIVEN: Recuperamos IDs reales de la BD (creados en setup)
        Long idCategoria = categoriaRepository.findAll().get(0).getIdCategoria();
        Long idSede = sedeRepository.findAll().get(0).getIdSede();

        // Construimos el DTO de solicitud con datos válidos
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Monitor 4K",
                "MON-4K-001",
                "Monitor de alta resolución",
                new BigDecimal("350.00"),
                true,
                idCategoria,
                new ProductoRequestDto.ImagenRequestDto("https://example.com/monitor.jpg", "Monitor 4k"),
                List.of(new ProductoRequestDto.StockSedeRequestDto(idSede, 50)));

        // 2. WHEN: Ejecutamos la petición POST al endpoint
        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON) // Indicamos que enviamos JSON
                .content(objectMapper.writeValueAsString(requestDto))) // Convertimos el objeto a String JSON

                // 3. THEN: Verificamos la respuesta
                .andExpect(status().isCreated()) // Verificamos estatus 201
                .andExpect(jsonPath("$.nombreProducto").value("Monitor 4K")) // Verificamos campos en la respuesta
                .andExpect(jsonPath("$.sku").value("MON-4K-001"))
                .andExpect(jsonPath("$.idProducto").exists()); // Verificamos que se generó un ID
    }

    /**
     * Test de seguridad: Intento de creación sin permisos suficientes.
     * Escenario: Usuario con rol CLIENTE (sin permiso de gestión).
     * Resultado esperado: Código HTTP 403 Forbidden.
     */
    @Test
    @DisplayName("POST /api/v1/productos - Debería fallar con 403 Forbidden si no tiene permisos")
    @WithMockUser(username = "usuario", roles = { "CLIENTE" }) // Usuario sin privilegios de admin
    void crearProducto_SinPermisos() throws Exception {
        // Datos de prueba (no importa si son válidos, debería fallar antes por
        // seguridad)
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Hack", "HACK-001", "Desc", BigDecimal.TEN, true, 1L,
                new ProductoRequestDto.ImagenRequestDto("https://example.com/hack.jpg", "Hack"),
                List.of());

        // Ejecutamos la petición y esperamos fallo de seguridad
        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden()); // Validamos el 403
    }
}
