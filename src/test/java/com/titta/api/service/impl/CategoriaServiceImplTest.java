package com.titta.api.service.impl;

import com.titta.api.features.category.dto.request.CategoriaRequestDto;
import com.titta.api.features.category.dto.response.CategoriaResponseDto;
import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.features.category.service.impl.CategoriaServiceImpl;
import com.titta.api.features.category.mapper.CategoriaMapper;
import com.titta.api.domain.model.Categoria;
import com.titta.api.domain.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private CategoriaRequestDto categoriaRequestDto;
    private Categoria categoria;
    private CategoriaResponseDto categoriaResponseDto;

    @BeforeEach
    void setUp() {
        // 1. Datos de entrada (Request)
        categoriaRequestDto = new CategoriaRequestDto("Electronica");

        // 2. Entidad del Modelo
        categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombreCategoria("Electrónica");

        // 3. Datos de salida (Response)
        categoriaResponseDto = new CategoriaResponseDto(1L, "Electrónica");
    }

    @Test
    void crearCategoria_cuandoNombreNoExiste_debeCrearCategoria() {
        // Arrange (Configurar el comportamiento de los mocks)
        when(categoriaRepository.existsByNombreCategoria("Electrónica")).thenReturn(false);
        when(categoriaMapper.toCategoria(any(CategoriaRequestDto.class))).thenReturn(categoria);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        when(categoriaMapper.toCategoriaResponseDto(any(Categoria.class))).thenReturn(categoriaResponseDto);

        // Act (Ejecutar el método que queremos probar)
        CategoriaResponseDto resultado = categoriaService.crearCategoria(categoriaRequestDto);

        // Assert (Verificar que el resultado es el esperado)
        assertNotNull(resultado);
        assertEquals("Electrónica", resultado.nombreCategoria());
        assertEquals(1L, resultado.idCategoria());
    }

    @Test
    void crearCategoria_cuandoNombreYaExiste_debeLanzarDuplicateResourceException() {
        // Arrange (Configurar el mock para simular que la categoría ya existe)
        when(categoriaRepository.existsByNombreCategoria("Electrónica")).thenReturn(true);

        // Act & Assert (Verificar que se lanza la excepción correcta)
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            categoriaService.crearCategoria(categoriaRequestDto);
        });

        // Opcional: Verificar el mensaje de la excepción
        assertEquals("Ya existe una categoría con el nombre 'Electrónica'.", exception.getMessage());
    }
}