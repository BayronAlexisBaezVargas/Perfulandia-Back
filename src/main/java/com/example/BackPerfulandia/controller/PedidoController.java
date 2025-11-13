package com.example.BackPerfulandia.controller;

import com.example.BackPerfulandia.model.DetallePedido;
import com.example.BackPerfulandia.model.Pedido;
import com.example.BackPerfulandia.model.Producto;
import com.example.BackPerfulandia.model.Usuario;
import com.example.BackPerfulandia.repository.PedidoRepository;
import com.example.BackPerfulandia.repository.ProductoRepository;
import com.example.BackPerfulandia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Endpoint para OBTENER todos los pedidos (para AdminOrders.js)
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    /**
     * Endpoint para OBTENER el historial de pedidos de un usuario (para Perfil.js)
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> getPedidosByUsuarioId(@PathVariable Long usuarioId) {
        List<Pedido> pedidos = pedidoRepository.findByUsuario_Id(usuarioId);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    /**
     * Endpoint para CREAR un nuevo pedido (para DetallePago.js)
     * * @Transactional asegura que si algo falla (ej. no hay stock),
     * la operación entera (el pedido y el descuento de stock) se revierte.
     * O todo funciona, o nada se guarda.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> createPedido(@RequestBody Pedido pedidoRequest) {
        try {
            // 1. Buscar al Usuario
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(pedidoRequest.getUsuario().getId());
            if (usuarioOpt.isEmpty()) {
                return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            Usuario usuario = usuarioOpt.get();

            // 2. Preparar el nuevo Pedido
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setUsuario(usuario);
            nuevoPedido.setFechaCreacion(LocalDateTime.now());
            nuevoPedido.setStatus("Pendiente"); // Estado inicial
            nuevoPedido.setDireccionEnvio(pedidoRequest.getDireccionEnvio());

            // Generar un número de pedido único (ej: #PF-f47ac10b)
            String numeroPedido = "PF-" + UUID.randomUUID().toString().substring(0, 8);
            nuevoPedido.setNumeroPedido(numeroPedido);

            BigDecimal totalPedido = BigDecimal.ZERO;
            List<DetallePedido> detallesProcesados = new ArrayList<>();

            // 3. Validar stock y procesar cada producto del pedido
            for (DetallePedido detalle : pedidoRequest.getDetalles()) {
                Optional<Producto> productoOpt = productoRepository.findById(detalle.getProducto().getId());
                if (productoOpt.isEmpty()) {
                    throw new RuntimeException("Producto con ID " + detalle.getProducto().getId() + " no encontrado.");
                }
                Producto producto = productoOpt.get();

                // Verificar stock
                if (producto.getStock() < detalle.getCantidad()) {
                    throw new RuntimeException("No hay stock suficiente para " + producto.getNombre());
                }

                // Descontar stock
                producto.setStock(producto.getStock() - detalle.getCantidad());
                productoRepository.save(producto); // Actualizar producto en la BD

                // Crear el detalle del pedido
                DetallePedido nuevoDetalle = new DetallePedido();
                nuevoDetalle.setProducto(producto);
                nuevoDetalle.setCantidad(detalle.getCantidad());
                // Usar el precio de la BD, no el que envía el cliente (por seguridad)
                nuevoDetalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecio()));

                detallesProcesados.add(nuevoDetalle);

                // Sumar al total
                totalPedido = totalPedido.add(nuevoDetalle.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevoDetalle.getCantidad())));
            }

            // 4. Guardar el Pedido completo
            nuevoPedido.setTotal(totalPedido);
            nuevoPedido.setDetalles(detallesProcesados); // Esto asigna los detalles al pedido

            Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

            return new ResponseEntity<>(pedidoGuardado, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Si algo falla (ej. stock), se retorna un error 400
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
