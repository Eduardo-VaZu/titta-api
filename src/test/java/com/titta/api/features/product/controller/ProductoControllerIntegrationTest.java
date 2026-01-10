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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @BeforeEach
    void setup() {

        Categoria categoria = new Categoria();
        categoria.setNombreCategoria("Electrónicos");
        categoria.setEstado(true);
        categoriaRepository.save(categoria);

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

    @Test
    @DisplayName("POST /api/v1/productos - Debería crear un producto y devolver 201 Created")
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMINISTRADOR", "GESTIONAR_PRODUCTOS" })
    void crearProducto_IntegrationTest() throws Exception {
        Long idCategoria = categoriaRepository.findAll().get(0).getIdCategoria();
        Long idSede = sedeRepository.findAll().get(0).getIdSede();

        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Monitor 4K",
                "MON-4K-001",
                "Monitor de alta resolución",
                new BigDecimal("350.00"),
                true,
                idCategoria,
                new ProductoRequestDto.ImagenRequestDto("https://example.com/monitor.jpg", "Monitor 4k"),
                List.of(new ProductoRequestDto.StockSedeRequestDto(idSede, 50)));

        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreProducto").value("Monitor 4K"))
                .andExpect(jsonPath("$.sku").value("MON-4K-001"))
                .andExpect(jsonPath("$.idProducto").exists());
    }

    @Test
    @DisplayName("POST /api/v1/productos - Debería fallar con 403 Forbidden si no tiene permisos")
    @WithMockUser(username = "usuario", roles = { "CLIENTE" })
    void crearProducto_SinPermisos() throws Exception {
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Hack", "HACK-001", "Desc", BigDecimal.TEN, true, 1L,
                new ProductoRequestDto.ImagenRequestDto("https://example.com/hack.jpg", "Hack"),
                List.of());

        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }
}
