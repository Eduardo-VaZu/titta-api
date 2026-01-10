package com.titta.api.features.product.service;

import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.Categoria;
import com.titta.api.domain.model.Producto;
import com.titta.api.domain.model.Sede;
import com.titta.api.domain.model.StockSede;
import com.titta.api.domain.repository.CartRepository;
import com.titta.api.domain.repository.CategoriaRepository;
import com.titta.api.domain.repository.MovimientoInventarioRepository;
import com.titta.api.domain.repository.ProductoRepository;
import com.titta.api.domain.repository.SedeRepository;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import com.titta.api.features.product.mapper.ProductoMapper;
import com.titta.api.features.product.service.impl.ProductoServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba unitaria para ProductoServiceImpl.
 * Utiliza Mockito para simular el compartamiento de los repositorios y mappers,
 * aislando la lógica de negocio del servicio.
 */
@ExtendWith(MockitoExtension.class) // Habilita la integración de Mockito con JUnit 5
public class ProductoServiceImplTest {

        @InjectMocks // Inyecta los mocks creados en la instancia de ProductoServiceImpl.
        private ProductoServiceImpl productoService;

        @Mock // Mock del repositorio de Productos.
        private ProductoRepository productoRepository;

        @Mock // Mock del repositorio de Categorías.
        private CategoriaRepository categoriaRepository;

        @Mock // Mock del repositorio de Sedes.
        private SedeRepository sedeRepository;

        @Mock // Mock del mapper para conversión de objetos.
        private ProductoMapper productoMapper;

        @Mock // Mock para registrar movimientos de inventario.
        private MovimientoInventarioRepository movimientoInventarioRepository;

        @Mock // Mock del repositorio de Carrito (si fuera necesario para otras pruebas).
        private CartRepository cartRepository;

        /**
         * Test de caso exitoso: Verifica que se pueda crear un producto correctamente
         * cuando todos los datos son válidos y las dependencias responden
         * correctamente.
         */
        @Test
        @DisplayName("crearProducto debería guardar el producto cuando los datos son válidos")
        void crearProducto_Exitoso() {
                // --- ARRANGE: Preparación de datos y mocks ---

                // 1. Crear el DTO de solicitud con todos los datos necesarios
                ProductoRequestDto requestDto = new ProductoRequestDto(
                                "Laptop Gamer",
                                "SKU-12345",
                                "Una laptop potente",
                                new BigDecimal("1500.00"),
                                true,
                                1L,
                                new ProductoRequestDto.ImagenRequestDto("http://imagen.com/img.jpg", "Laptop Gamer"),
                                List.of(new ProductoRequestDto.StockSedeRequestDto(1L, 10)));

                // 2. Simular las entidades que se recuperarán de la base de datos o se crearán
                Categoria categoriaMock = new Categoria();
                categoriaMock.setIdCategoria(1L);

                Sede sedeMock = new Sede();
                sedeMock.setIdSede(1L);

                Producto productoMock = new Producto();
                productoMock.setIdProducto(100L);

                StockSede stockMock = new StockSede();
                stockMock.setSede(sedeMock);
                stockMock.setCantidad(10);
                productoMock.setStocks(Set.of(stockMock));

                // 3. Simular el DTO de respuesta que devolverá el mapper
                ProductoResponseDto responseDtoMock = new ProductoResponseDto(
                                100L,
                                "Laptop Gamer",
                                "SKU-12345",
                                "Una laptop potente",
                                new BigDecimal("1500.00"),
                                true,
                                1L,
                                new ProductoResponseDto.ImagenResponseDto(1L, "http://imagen.com/img.jpg",
                                                "Laptop Gamer"),
                                Collections.emptyList());

                // 4. Configurar el comportamiento de los Mocks (Stubbing)

                // Simular que el SKU no existe (para permitir la creación)
                when(productoRepository.existsBySku("SKU-12345")).thenReturn(false);
                // Simular búsqueda de categorías y sedes existentes
                when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMock));
                when(sedeRepository.findAllById(anyList())).thenReturn(List.of(sedeMock));

                // Simular el mapeo de DTO a Entidad
                when(productoMapper.toProducto(any(ProductoRequestDto.class), any(Categoria.class), anyList()))
                                .thenReturn(productoMock);

                // Simular el guardado en base de datos
                when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

                // Simular el mapeo de Entidad a DTO de respuesta
                when(productoMapper.toProductoResponseDto(any(Producto.class)))
                                .thenReturn(responseDtoMock);

                // --- ACT: Ejecución del método a probar ---
                ProductoResponseDto resultado = productoService.crearProducto(requestDto);

                // --- ASSERT: Verificaciones ---

                // Verificar que el resultado no sea nulo y contenga los datos esperados
                assertNotNull(resultado);
                assertEquals("Laptop Gamer", resultado.nombreProducto());

                // Verificar que se llamaron a los métodos críticos de los repositorios
                verify(productoRepository).save(any(Producto.class)); // Se guardó el producto
                verify(movimientoInventarioRepository).saveAll(anyList()); // Se registraron movimientos de inventario

        }

        /**
         * Test de excepción: Verifica que se lance DuplicateResourceException
         * si se intenta crear un producto con un SKU que ya existe.
         */
        @Test
        @DisplayName("crearProducto debería lanzar DuplicateResourceException si el SKU ya existe")
        void crearProducto_SkuDuplicado() {
                // ARRANGE
                ProductoRequestDto requestDto = new ProductoRequestDto(
                                "Mouse", "SKU-DUPLICADO", "Desc", BigDecimal.TEN, true, 1L,
                                new ProductoRequestDto.ImagenRequestDto("http://dummy.url", "Alt Text"),
                                Collections.emptyList());

                // Mockear comportamiento: el SKU YA existe
                when(productoRepository.existsBySku("SKU-DUPLICADO")).thenReturn(true);

                // ACT & ASSERT: Esperar excepción
                assertThrows(DuplicateResourceException.class, () -> {
                        productoService.crearProducto(requestDto);
                });

                // Verificar que NUNCA se intente guardar si ya existe el SKU
                verify(productoRepository, never()).save(any());
        }

        /**
         * Test de excepción: Verifica que se lance ResourceNotFoundException
         * si se intenta asociar un producto a una categoría que no existe en BD.
         */
        @Test
        @DisplayName("crearProducto debería lanzar ResourceNotFoundException si la categoría no existe")
        void crearProducto_CategoriaNoExiste() {
                // ARRANGE
                ProductoRequestDto requestDto = new ProductoRequestDto(
                                "Teclado", "SKU-NUEVO", "Desc", BigDecimal.TEN, true, 999L,
                                new ProductoRequestDto.ImagenRequestDto("http://dummy.url", "Alt Text"),
                                Collections.emptyList());

                when(productoRepository.existsBySku("SKU-NUEVO")).thenReturn(false);
                // Mockear comportamiento: la categoría NO se encuentra
                when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

                // ACT & ASSERT: Esperar excepción
                assertThrows(ResourceNotFoundException.class, () -> {
                        productoService.crearProducto(requestDto);
                });

                // Verificar que NUNCA se intente guardar sin categoría válida
                verify(productoRepository, never()).save(any());
        }
}
