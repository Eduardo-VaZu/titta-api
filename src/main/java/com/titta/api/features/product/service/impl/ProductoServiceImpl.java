package com.titta.api.features.product.service.impl;

import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.Categoria;
import com.titta.api.domain.model.Producto;
import com.titta.api.domain.model.Sede;
import com.titta.api.domain.repository.CategoriaRepository;
import com.titta.api.domain.repository.ProductoRepository;
import com.titta.api.domain.repository.SedeRepository;
import com.titta.api.features.product.dto.request.ProductoBatchRequestDto;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.request.ProductoUpdateDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import com.titta.api.features.product.mapper.ProductoMapper;
import com.titta.api.features.product.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private ProductoMapper productoMapper;

    @Override
    @Transactional
    public ProductoResponseDto crearProducto(ProductoRequestDto requestDto) {

        if (productoRepository.existsBySku(requestDto.sku())) {
            throw new DuplicateResourceException("El SKU '" + requestDto.sku() + "' ya está registrado.");
        }

        Categoria categoria = categoriaRepository.findById(requestDto.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + requestDto.idCategoria()));

        List<Long> sedeIds = requestDto.stocks().stream()
                .map(ProductoRequestDto.StockSedeRequestDto::idSede)
                .distinct()
                .collect(Collectors.toList());

        List<Sede> sedesEncontradas = sedeRepository.findAllById(sedeIds);

        if (sedesEncontradas.size() != sedeIds.size()) {
            List<Long> idsEncontrados = sedesEncontradas.stream().map(Sede::getIdSede).toList();
            List<Long> idsFaltantes = sedeIds.stream()
                    .filter(id -> !idsEncontrados.contains(id))
                    .toList();
            throw new ResourceNotFoundException("No se encontraron las siguientes Sedes: " + idsFaltantes);
        }

        Producto producto = productoMapper.toProducto(requestDto, categoria, sedesEncontradas);

        Producto productoGuardado = productoRepository.save(producto);

        return productoMapper.toProductoResponseDto(productoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDto getProductoById(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));

        return productoMapper.toProductoResponseDto(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDto> getAllProductos(Boolean soloActivos) {
        List<Producto> productos;

        if (soloActivos != null) {
            productos = productoRepository.findByEstadoProducto(soloActivos);
        }else{
            productos = productoRepository.findAll();
        }

        return productos.stream()
                .map(productoMapper::toProductoResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoResponseDto updateProducto(Long idProducto, ProductoUpdateDto updateDto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));

        Categoria categoria = categoriaRepository.findById(updateDto.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + updateDto.idCategoria()));

        producto.setNombreProducto(updateDto.nombreProducto());
        producto.setDescripcion(updateDto.descripcion());
        producto.setPrecio(updateDto.precio());
        producto.setEstadoProducto(updateDto.estadoProducto());
        producto.setCategoria(categoria);

        Producto productoActualizado = productoRepository.save(producto);
        return productoMapper.toProductoResponseDto(productoActualizado);
    }

    @Override
    @Transactional
    public void deleteProducto(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));

        producto.setEstadoProducto(false);

        productoRepository.save(producto);
    }

    //    @Override
//    @Transactional
//    public List<ProductoResponseDto> crearProductosBatch(ProductoBatchRequestDto batchRequestDto) {
//        List<ProductoRequestDto> dtos = batchRequestDto.productos();
//
//        // --- 1. VALIDACIÓN DE SKUs ---
//        Set<String> skusDelRequest = dtos.stream()
//                .map(ProductoRequestDto::sku)
//                .collect(Collectors.toSet());
//
//        // a) Validar duplicados dentro del propio request
//        if (skusDelRequest.size() != dtos.size()) {
//            // Encontrar cuáles están duplicados para un mensaje de error claro
//            String duplicados = dtos.stream()
//                    .map(ProductoRequestDto::sku)
//                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
//                    .entrySet().stream()
//                    .filter(entry -> entry.getValue() > 1)
//                    .map(Map.Entry::getKey)
//                    .collect(Collectors.joining(", "));
//            throw new DuplicateResourceException("El request contiene SKUs duplicados: " + duplicados);
//        }
//
//        // b) Validar duplicados contra la Base de Datos
//        List<Producto> productosExistentes = productoRepository.findBySkuIn(List.copyOf(skusDelRequest));
//        if (!productosExistentes.isEmpty()) {
//            String skusEncontrados = productosExistentes.stream()
//                    .map(Producto::getSku)
//                    .collect(Collectors.joining(", "));
//            throw new DuplicateResourceException("Los siguientes SKUs ya existen en la base de datos: " + skusEncontrados);
//        }
//
//        // --- 2. VALIDACIÓN DE CATEGORÍAS ---
//        Set<Long> categoriaIds = dtos.stream()
//                .map(ProductoRequestDto::idCategoria)
//                .collect(Collectors.toSet());
//
//        Map<Long, Categoria> categoriasMap = categoriaRepository.findAllById(categoriaIds).stream()
//                .collect(Collectors.toMap(Categoria::getIdCategoria, Function.identity()));
//
//        validarRecursosEncontrados(categoriaIds, categoriasMap.keySet(), "Categorías");
//
//        // --- 3. VALIDACIÓN DE SEDES ---
//        Set<Long> sedeIds = dtos.stream()
//                .flatMap(dto -> dto.stocks().stream()) // Aplana todas las listas de stocks
//                .map(ProductoRequestDto.StockSedeRequestDto::idSede) // Obtiene todos los idSede
//                .collect(Collectors.toSet()); // Únicos
//
//        Map<Long, Sede> sedesMap = sedeRepository.findAllById(sedeIds).stream()
//                .collect(Collectors.toMap(Sede::getIdSede, Function.identity()));
//
//        validarRecursosEncontrados(sedeIds, sedesMap.keySet(), "Sedes");
//
//        // --- 4. MAPEO Y GUARDADO ---
//        List<Producto> productosAGuardar = dtos.stream().map(dto -> {
//            Categoria categoria = categoriasMap.get(dto.idCategoria());
//
//            // Obtenemos la lista de entidades Sede para este producto específico
//            List<Sede> sedesParaEsteProducto = dto.stocks().stream()
//                    .map(stockDto -> sedesMap.get(stockDto.idSede()))
//                    .collect(Collectors.toList());
//
//            return productoMapper.toProducto(dto, categoria, sedesParaEsteProducto);
//        }).collect(Collectors.toList());
//
//        // Guardamos todos los productos en una sola operación
//        List<Producto> productosGuardados = productoRepository.saveAll(productosAGuardar);
//
//        // --- 5. RESPUESTA ---
//        return productosGuardados.stream()
//                .map(productoMapper::toProductoResponseDto)
//                .collect(Collectors.toList());
//    }
//
//    private void validarRecursosEncontrados(Set<Long> idsSolicitados, Set<Long> idsEncontrados, String nombreRecurso) {
//        if (idsSolicitados.size() != idsEncontrados.size()) {
//            Set<Long> idsFaltantes = Set.copyOf(idsSolicitados);
//            idsFaltantes.removeAll(idsEncontrados);
//
//            Set<Long> faltantes = idsSolicitados.stream()
//                    .filter(id -> !idsEncontrados.contains(id))
//                    .collect(Collectors.toSet());
//            throw new ResourceNotFoundException("No se encontraron los siguientes recursos ('" + nombreRecurso + "') con IDs: " + faltantes);
//        }
//    }
}