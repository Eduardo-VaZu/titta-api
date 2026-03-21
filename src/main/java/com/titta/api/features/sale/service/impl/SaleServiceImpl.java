package com.titta.api.features.sale.service.impl;

import com.stripe.exception.StripeException;
import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.Carrito;
import com.titta.api.domain.model.DetalleVenta;
import com.titta.api.domain.model.EstadoVenta;
import com.titta.api.domain.model.ItemCarrito;
import com.titta.api.domain.model.MetodoPago;
import com.titta.api.domain.model.Usuario;
import com.titta.api.domain.model.Venta;
import com.titta.api.domain.model.enums.EstadoCarritoEnum;
import com.titta.api.domain.repository.CartRepository;
import com.titta.api.domain.repository.EstadoVentaRepository;
import com.titta.api.domain.repository.MetodoPagoRepository;
import com.titta.api.domain.repository.UsuarioRepository;
import com.titta.api.domain.repository.VentaRepository;
import com.titta.api.features.inventory.service.InventarioService;
import com.titta.api.features.payment.dto.PaymentIntentDto;
import com.titta.api.features.payment.service.PaymentService;
import com.titta.api.features.sale.dto.request.SaleRequestDto;
import com.titta.api.features.sale.dto.response.SaleResponseDto;
import com.titta.api.features.sale.mapper.VentaMapper;
import com.titta.api.features.sale.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private static final String METODO_TARJETA = "TARJETA";
    private static final String ESTADO_COMPLETADA = "COMPLETADA";
    private static final String ESTADO_PENDIENTE = "PENDIENTE";

    private final CartRepository cartRepository;
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;
    private final EstadoVentaRepository estadoVentaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final PaymentService paymentService;
    private final VentaMapper ventaMapper;

    @Override
    @Transactional
    public SaleResponseDto realizarVenta(SaleRequestDto request) {
        Usuario usuario = getAuthenticatedUser();

        Carrito carrito = cartRepository.findByUsuarioIdAndEstadoForUpdate(usuario.getIdUsuario(), EstadoCarritoEnum.ACTIVO)
                .orElseThrow(() -> new ResourceNotFoundException("No tienes un carrito activo para procesar."));

        if (carrito.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito esta vacio.");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(request.idMetodoPago())
                .orElseThrow(() -> new ResourceNotFoundException("Metodo de pago no encontrado."));

        boolean pagoConTarjeta = METODO_TARJETA.equalsIgnoreCase(metodoPago.getNombreMetodo());
        String nombreEstadoVenta = pagoConTarjeta ? ESTADO_PENDIENTE : ESTADO_COMPLETADA;

        EstadoVenta estadoVenta = estadoVentaRepository.findByNombreEstado(nombreEstadoVenta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estado de venta '" + nombreEstadoVenta + "' no configurado en BD."));

        Venta venta = ventaMapper.toVentaEntity(usuario, carrito, metodoPago, estadoVenta);

        BigDecimal totalVenta = calcularTotal(carrito);
        venta.setTotal(totalVenta);

        if (pagoConTarjeta) {
            try {
                PaymentIntentDto paymentIntent = paymentService.createPaymentIntent(totalVenta);
                venta.setIdTransaccion(paymentIntent.id());
            } catch (StripeException e) {
                throw new IllegalStateException("Error al crear el intento de pago con Stripe", e);
            }
        }

        for (ItemCarrito item : carrito.getItems()) {
            inventarioService.reducirStockPorVenta(
                    item.getProducto().getIdProducto(),
                    carrito.getSede().getIdSede(),
                    item.getCantidad());

            DetalleVenta detalle = ventaMapper.toDetalleVentaEntity(item, venta);
            venta.getDetalles().add(detalle);
        }

        Venta ventaGuardada = ventaRepository.save(venta);

        carrito.setEstado(EstadoCarritoEnum.COMPLETADO);
        cartRepository.save(carrito);

        return ventaMapper.toVentaResponseDto(ventaGuardada);
    }

    private BigDecimal calcularTotal(Carrito carrito) {
        return carrito.getItems().stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Usuario getAuthenticatedUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userEmail));
    }
}
