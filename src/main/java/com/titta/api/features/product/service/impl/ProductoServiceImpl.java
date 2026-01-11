package com.titta.api.features.product.service.impl;

import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.*;
import com.titta.api.domain.model.enums.EstadoCarritoEnum;
import com.titta.api.domain.model.enums.TipoMovimientoInventario;
import com.titta.api.domain.repository.*;
import com.titta.api.features.product.dto.request.ProductoBatchRequestDto;
import com.titta.api.features.product.dto.request.ProductoRequestDto;
import com.titta.api.features.product.dto.request.ProductoUpdateDto;
import com.titta.api.features.product.dto.response.ProductoResponseDto;
import com.titta.api.features.product.mapper.ProductoMapper;
import com.titta.api.features.product.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

        private final ProductoRepository productoRepository;
        private final CategoriaRepository categoriaRepository;
        private final SedeRepository sedeRepository;
        private final ProductoMapper productoMapper;
        private final MovimientoInventarioRepository movimientoInventarioRepository;
        private final CartRepository cartRepository;

        @Override
        @Transactional
        @CacheEvict(value = "productos", allEntries = true)
        public ProductoResponseDto crearProducto(ProductoRequestDto requestDto) {

                if (productoRepository.existsBySku(requestDto.sku())) {
                        throw new DuplicateResourceException("El SKU '" + requestDto.sku() + "' ya está registrado.");
                }

                Categoria categoria = categoriaRepository.findById(requestDto.idCategoria())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Categoría no encontrada con ID: " + requestDto.idCategoria()));

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

                List<MovimientoInventario> movimientos = new ArrayList<>();
                for (StockSede stock : productoGuardado.getStocks()) {
                        MovimientoInventario movimiento = MovimientoInventario.builder()
                                        .producto(productoGuardado)
                                        .sede(stock.getSede())
                                        .tipoMovimiento(TipoMovimientoInventario.INGRESO_COMPRA)
                                        .cantidad(stock.getCantidad())
                                        .fechaMovimiento(LocalDate.now())
                                        .razon("Stock inicial al crear producto")
                                        .build();
                        movimientos.add(movimiento);
                }
                movimientoInventarioRepository.saveAll(movimientos);

                return productoMapper.toProductoResponseDto(productoGuardado);
        }

        @Override
        @Transactional(readOnly = true)
        @Cacheable(value = "productos", key = "#idProducto")
        public ProductoResponseDto getProductoById(Long idProducto) {
                Producto producto = productoRepository.findById(idProducto)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Producto no encontrado con ID: " + idProducto));

                return productoMapper.toProductoResponseDto(producto);
        }

        @Override
        @Transactional(readOnly = true)
        @Cacheable(value = "productos")
        public Page<ProductoResponseDto> getAllProductos(Boolean soloActivos, Pageable pageable) {
                Page<Producto> productosPage = (soloActivos != null)
                                ? productoRepository.findByEstadoProducto(soloActivos, pageable)
                                : productoRepository.findAll(pageable);

                return productosPage.map(productoMapper::toProductoResponseDto);
        }

        @Override
        @Transactional
        @CacheEvict(value = { "producto", "productos" }, allEntries = true)
        public ProductoResponseDto updateProducto(Long idProducto, ProductoUpdateDto updateDto) {
                Producto producto = productoRepository.findById(idProducto)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Producto no encontrado con ID: " + idProducto));

                Categoria categoria = categoriaRepository.findById(updateDto.idCategoria())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Categoría no encontrada con ID: " + updateDto.idCategoria()));

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
        @CacheEvict(value = { "producto", "productos" }, allEntries = true)
        public void deleteProducto(Long idProducto) {
                Producto producto = productoRepository.findById(idProducto)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Producto no encontrado con ID: " + idProducto));

                List<Carrito> carritosActivos = cartRepository.findAllByEstado(EstadoCarritoEnum.ACTIVO);
                boolean enCarritoActivo = carritosActivos.stream()
                                .flatMap(carrito -> carrito.getItems().stream())
                                .anyMatch(item -> item.getProducto().getIdProducto().equals(idProducto));

                if (enCarritoActivo) {
                        throw new DataIntegrityViolationException(
                                        "No se puede desactivar el producto (ID: " + idProducto
                                                        + "). Aún está en carritos activos.");
                }

                producto.setEstadoProducto(false);

                productoRepository.save(producto);
        }

        @Override
        @Transactional
        public List<ProductoResponseDto> crearProductosBatch(ProductoBatchRequestDto batchRequestDto) {
                List<ProductoRequestDto> dtos = batchRequestDto.productos();

                Set<String> skusDelRequest = dtos.stream()
                                .map(ProductoRequestDto::sku)
                                .collect(Collectors.toSet());

                if (skusDelRequest.size() != dtos.size()) {
                        String duplicados = dtos.stream()
                                        .map(ProductoRequestDto::sku)
                                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                                        .entrySet().stream()
                                        .filter(entry -> entry.getValue() > 1)
                                        .map(Map.Entry::getKey)
                                        .collect(Collectors.joining(", "));
                        throw new DuplicateResourceException("El request contiene SKUs duplicados: " + duplicados);
                }

                List<Producto> productosExistentes = productoRepository.findBySkuIn(List.copyOf(skusDelRequest));
                if (!productosExistentes.isEmpty()) {
                        String skusEncontrados = productosExistentes.stream()
                                        .map(Producto::getSku)
                                        .collect(Collectors.joining(", "));
                        throw new DuplicateResourceException(
                                        "Los siguientes SKUs ya existen en la base de datos: " + skusEncontrados);
                }

                Set<Long> categoriaIds = dtos.stream()
                                .map(ProductoRequestDto::idCategoria)
                                .collect(Collectors.toSet());

                Map<Long, Categoria> categoriasMap = categoriaRepository.findAllById(categoriaIds).stream()
                                .collect(Collectors.toMap(Categoria::getIdCategoria, Function.identity()));

                validarRecursosEncontrados(categoriaIds, categoriasMap.keySet(), "Categorías");

                Set<Long> sedeIds = dtos.stream()
                                .flatMap(dto -> dto.stocks().stream())
                                .map(ProductoRequestDto.StockSedeRequestDto::idSede)
                                .collect(Collectors.toSet());

                Map<Long, Sede> sedesMap = sedeRepository.findAllById(sedeIds).stream()
                                .collect(Collectors.toMap(Sede::getIdSede, Function.identity()));

                validarRecursosEncontrados(sedeIds, sedesMap.keySet(), "Sedes");

                List<Producto> productosAGuardar = dtos.stream().map(dto -> {
                        Categoria categoria = categoriasMap.get(dto.idCategoria());

                        List<Sede> sedesParaEsteProducto = dto.stocks().stream()
                                        .map(stockDto -> sedesMap.get(stockDto.idSede()))
                                        .collect(Collectors.toList());

                        return productoMapper.toProducto(dto, categoria, sedesParaEsteProducto);
                }).collect(Collectors.toList());

                List<Producto> productosGuardados = productoRepository.saveAll(productosAGuardar);

                List<MovimientoInventario> movimientosAGuardar = new ArrayList<>();
                for (Producto producto : productosGuardados) {
                        for (StockSede stock : producto.getStocks()) {
                                MovimientoInventario movimiento = MovimientoInventario.builder()
                                                .producto(producto)
                                                .sede(stock.getSede())
                                                .tipoMovimiento(TipoMovimientoInventario.INGRESO_COMPRA)
                                                .cantidad(stock.getCantidad())
                                                .fechaMovimiento(LocalDate.now())
                                                .razon("Stock inicial en creación por lote")
                                                .build();
                                movimientosAGuardar.add(movimiento);
                        }
                }
                movimientoInventarioRepository.saveAll(movimientosAGuardar);

                return productosGuardados.stream()
                                .map(productoMapper::toProductoResponseDto)
                                .collect(Collectors.toList());
        }

        private void validarRecursosEncontrados(Set<Long> idsSolicitados, Set<Long> idsEncontrados,
                        String nombreRecurso) {
                if (idsSolicitados.size() != idsEncontrados.size()) {
                        Set<Long> idsFaltantes = idsSolicitados.stream()
                                        .filter(id -> !idsEncontrados.contains(id))
                                        .collect(Collectors.toSet());
                        throw new ResourceNotFoundException("No se encontraron los siguientes recursos ('"
                                        + nombreRecurso + "') con IDs: " + idsFaltantes);
                }
        }
}