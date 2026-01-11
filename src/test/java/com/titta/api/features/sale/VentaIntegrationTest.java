package com.titta.api.features.sale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.titta.api.domain.model.*;
import com.titta.api.domain.model.enums.EstadoCarritoEnum;
import com.titta.api.domain.model.enums.RolEnum;
import com.titta.api.domain.repository.*;
import com.titta.api.features.sale.dto.request.SaleRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class VentaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private StockSedeRepository stockSedeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EstadoVentaRepository estadoVentaRepository;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long productoId;
    private Long sedeId;
    private Long usuarioId;
    private Long metodoPagoId;

    @BeforeEach
    void setUp() {

        Categoria categoria = new Categoria();
        categoria.setNombreCategoria("Electrónicos");
        categoria.setEstado(true);
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombreProducto("Laptop Gamer");
        producto.setSku("LAP-001");
        producto.setPrecio(new BigDecimal("1500.00"));
        producto.setCategoria(categoria);
        producto.setEstadoProducto(true);
        producto = productoRepository.save(producto);
        productoId = producto.getIdProducto();

        Direccion direccion = new Direccion();
        direccion.setCiudad("Ciudad 1");
        direccion.setCalle("Departamento 1");
        direccion.setNumeroExterior("123");
        direccion.setEstadoProvincial("Provincia 1");

        Sede sede = new Sede();
        sede.setNombreSede("Sede Central");
        sede.setDireccion(direccion);
        sede.setEstado(true);
        sede = sedeRepository.save(sede);
        sedeId = sede.getIdSede();

        StockSede stock = new StockSede();
        stock.setId(new StockSedeId(sedeId, productoId));
        stock.setSede(sede);
        stock.setProducto(producto);
        stock.setCantidad(10);
        stockSedeRepository.save(stock);

        Rol rol = new Rol();
        rol.setNombreRol(RolEnum.EMPLEADO);
        rol = rolRepository.save(rol);

        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setApellidoPaterno("Perez");
        usuario.setApellidoMaterno("Lopez");
        usuario.setEmail("vendedor@example.com");
        usuario.setRol(rol);
        usuario.setEstadoUsuario(true);
        usuario = usuarioRepository.save(usuario);
        usuarioId = usuario.getIdUsuario();

        EstadoVenta estadoVenta = new EstadoVenta();
        estadoVenta.setNombreEstado("COMPLETADA");
        estadoVentaRepository.save(estadoVenta);

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setNombreMetodo("EFECTIVO");
        metodoPago = metodoPagoRepository.save(metodoPago);
        metodoPagoId = metodoPago.getIdMetodoPago();
    }

    @Test
    @WithMockUser(username = "vendedor@example.com", authorities = { "USAR_CARRITO" })
    void realizarVentaExitosa_deberiaRetornar200_yDescontarStock() throws Exception {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Sede sede = sedeRepository.findById(sedeId).orElseThrow();
        Producto producto = productoRepository.findById(productoId).orElseThrow();

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setSede(sede);
        carrito.setEstado(EstadoCarritoEnum.ACTIVO);

        ItemCarrito item = new ItemCarrito();
        item.setId(new ItemCarritoId(null, productoId));
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(2);
        item.setPrecioUnitario(producto.getPrecio());

        Set<ItemCarrito> items = new HashSet<>();
        items.add(item);
        carrito.setItems(items);

        cartRepository.save(carrito);

        SaleRequestDto request = new SaleRequestDto(metodoPagoId);

        mockMvc.perform(post("/api/v1/ventas/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3000.00))
                .andExpect(jsonPath("$.idVenta").exists());

        StockSede stockActualizado = stockSedeRepository.findById(new StockSedeId(sedeId, productoId)).orElseThrow();

        if (stockActualizado.getCantidad() != 8) {
            throw new AssertionError(
                    "El stock no se descontó correctamente. Esperado: 8, Actual: " + stockActualizado.getCantidad());
        }

        Carrito carritoPostVenta = cartRepository.findById(carrito.getId()).orElseThrow();
        if (carritoPostVenta.getEstado() != EstadoCarritoEnum.COMPLETADO) {
            throw new AssertionError("El estado del carrito no cambió a COMPLETADO");
        }
    }

    @Test
    @WithMockUser(username = "vendedor@example.com", authorities = { "USAR_CARRITO" })
    void realizarVenta_sinStockSuficiente_deberiaFallar() throws Exception {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Sede sede = sedeRepository.findById(sedeId).orElseThrow();
        Producto producto = productoRepository.findById(productoId).orElseThrow();

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setSede(sede);
        carrito.setEstado(EstadoCarritoEnum.ACTIVO);

        ItemCarrito item = new ItemCarrito();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(20);
        item.setPrecioUnitario(producto.getPrecio());
        item.setId(new ItemCarritoId(null, productoId));

        Set<ItemCarrito> items = new HashSet<>();
        items.add(item);
        carrito.setItems(items);

        cartRepository.save(carrito);

        SaleRequestDto request = new SaleRequestDto(metodoPagoId);

        mockMvc.perform(post("/api/v1/ventas/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}