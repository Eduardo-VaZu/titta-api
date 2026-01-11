package com.titta.api.features.cart.service.impl;

import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.*;
import com.titta.api.domain.model.enums.EstadoCarritoEnum;
import com.titta.api.domain.repository.*;
import com.titta.api.features.cart.dto.request.AddToCartRequestDto;
import com.titta.api.features.cart.dto.request.UpdateCartItemRequestDto;
import com.titta.api.features.cart.dto.response.CartResponseDto;
import com.titta.api.features.cart.mapper.CartMapper;
import com.titta.api.features.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final SedeRepository sedeRepository;
    private final StockSedeRepository stockSedeRepository;
    private final CartMapper cartMapper;

    private static final String ESTADO_CARRITO_ACTIVO = "ACTIVO";

    @Override
    public CartResponseDto addItemToCart(AddToCartRequestDto requestDto) {

        Usuario usuario = getAuthenticatedUser();

        Producto producto = productoRepository.findById(requestDto.idProducto())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con ID: " + requestDto.idProducto()));

        Sede sede = sedeRepository.findById(requestDto.idSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + requestDto.idSede()));

        Carrito carrito = getOrCreateActiveCart(usuario);

        if (carrito.getSede() == null) {
            carrito.setSede(sede);
            log.info("Carrito (ID: {}) vinculado a Sede (ID: {})", carrito.getId(), sede.getIdSede());
        } else if (!carrito.getSede().getIdSede().equals(requestDto.idSede())) {
            throw new IllegalArgumentException(
                    "No puedes añadir productos de diferentes sedes. Tu carrito actual está vinculado a la sede: "
                            + carrito.getSede().getNombreSede());
        }

        StockSede stock = stockSedeRepository.findById(new StockSedeId(requestDto.idProducto(), requestDto.idSede()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay stock registrado para este producto en la sede seleccionada."));

        Optional<ItemCarrito> itemExistenteOpt = carrito.getItems().stream()
                .filter(item -> item.getId().getIdProducto().equals(requestDto.idProducto()))
                .findFirst();

        int cantidadTotalEnCarrito;
        if (itemExistenteOpt.isPresent()) {
            ItemCarrito itemExistente = itemExistenteOpt.get();
            cantidadTotalEnCarrito = itemExistente.getCantidad() + requestDto.cantidad();
        } else {
            cantidadTotalEnCarrito = requestDto.cantidad();
        }

        if (stock.getCantidad() < cantidadTotalEnCarrito) {
            throw new IllegalArgumentException("Stock insuficiente. Stock disponible: " + stock.getCantidad()
                    + ", intentas añadir: " + cantidadTotalEnCarrito);
        }

        if (itemExistenteOpt.isPresent()) {
            itemExistenteOpt.get().setCantidad(cantidadTotalEnCarrito);
            log.info("Cantidad actualizada para Producto (ID: {}) en Carrito (ID: {})", requestDto.idProducto(),
                    carrito.getId());
        } else {
            ItemCarrito nuevoItem = ItemCarrito.builder()
                    .id(new ItemCarritoId(carrito.getId(), producto.getIdProducto()))
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(requestDto.cantidad())
                    .precioUnitario(producto.getPrecio())
                    .build();
            carrito.getItems().add(nuevoItem);
            log.info("Producto (ID: {}) añadido a Carrito (ID: {})", requestDto.idProducto(), carrito.getId());
        }

        Carrito carritoGuardado = cartRepository.save(carrito);
        return cartMapper.toCartResponseDto(carritoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getActiveCart() {
        Usuario usuario = getAuthenticatedUser();
        Carrito carrito = getOrCreateActiveCart(usuario);
        return cartMapper.toCartResponseDto(carrito);
    }

    @Override
    public CartResponseDto updateItemQuantity(Long idProducto, UpdateCartItemRequestDto requestDto) {
        Usuario usuario = getAuthenticatedUser();
        Carrito carrito = getOrCreateActiveCart(usuario);

        if (carrito.getSede() == null) {
            throw new ResourceNotFoundException(
                    "No se puede actualizar la cantidad, el carrito está vacío y no tiene sede asignada.");
        }

        ItemCarrito item = findItemInCart(carrito, idProducto);

        StockSede stock = stockSedeRepository.findById(new StockSedeId(idProducto, carrito.getSede().getIdSede()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay stock registrado para este producto en la sede del carrito."));

        int nuevaCantidad = requestDto.cantidad();
        if (stock.getCantidad() < nuevaCantidad) {
            throw new IllegalArgumentException("Stock insuficiente. Stock disponible: " + stock.getCantidad()
                    + ", intentas asignar: " + nuevaCantidad);
        }

        item.setCantidad(nuevaCantidad);
        log.info("Cantidad actualizada a {} para Producto (ID: {}) en Carrito (ID: {})", nuevaCantidad, idProducto,
                carrito.getId());

        Carrito carritoGuardado = cartRepository.save(carrito);
        return cartMapper.toCartResponseDto(carritoGuardado);
    }

    @Override
    public CartResponseDto removeItemFromCart(Long idProducto) {
        Usuario usuario = getAuthenticatedUser();
        Carrito carrito = getOrCreateActiveCart(usuario);

        ItemCarrito item = findItemInCart(carrito, idProducto);

        carrito.getItems().remove(item);
        log.info("Producto (ID: {}) eliminado del Carrito (ID: {})", idProducto, carrito.getId());

        Carrito carritoGuardado = cartRepository.save(carrito);
        return cartMapper.toCartResponseDto(carritoGuardado);
    }

    @Override
    public CartResponseDto clearCart() {
        Usuario usuario = getAuthenticatedUser();
        Carrito carrito = getOrCreateActiveCart(usuario);

        carrito.getItems().clear();
        log.info("Todos los items eliminados del Carrito (ID: {})", carrito.getId());

        Carrito carritoGuardado = cartRepository.save(carrito);
        return cartMapper.toCartResponseDto(carritoGuardado);
    }

    private Usuario getAuthenticatedUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado en el contexto de seguridad: " + userEmail));
    }

    private Carrito getOrCreateActiveCart(Usuario usuario) {
        return cartRepository.findByUsuario_IdUsuarioAndEstado(usuario.getIdUsuario(), EstadoCarritoEnum.ACTIVO)
                .orElseGet(() -> {
                    log.info("No se encontró carrito activo para Usuario (ID: {}). Creando uno nuevo.",
                            usuario.getIdUsuario());
                    Carrito nuevoCarrito = Carrito.builder()
                            .usuario(usuario)
                            .estado(EstadoCarritoEnum.ACTIVO)
                            .build();
                    return cartRepository.save(nuevoCarrito);
                });
    }

    private ItemCarrito findItemInCart(Carrito carrito, Long idProducto) {
        return carrito.getItems().stream()
                .filter(item -> item.getId().getIdProducto().equals(idProducto))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El producto (ID: " + idProducto + ") no se encuentra en tu carrito."));
    }
}
