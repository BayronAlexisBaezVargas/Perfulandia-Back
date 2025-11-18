package com.example.BackPerfulandia.controller;

import com.example.BackPerfulandia.model.Producto;
import com.example.BackPerfulandia.repository.PedidoRepository; // Importamos el repo de pedidos
import com.example.BackPerfulandia.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // Inyectamos PedidoRepository para poder verificar si hay pedidos pendientes
    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Obtener todos los productos.
     * Nota: El frontend se encarga de filtrar los 'activos' para los clientes.
     * El admin recibe todos para poder ver el historial si es necesario.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    /**
     * Obtener un producto por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> productoOptional = productoRepository.findById(id);

        return productoOptional
                .map(producto -> new ResponseEntity<>(producto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Crear un nuevo producto.
     */
    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        producto.setId(null); // Asegurar que es una creación

        // Aseguramos que el nuevo producto nazca activo
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }

        Producto nuevoProducto = productoRepository.save(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    /**
     * Actualizar un producto existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto productoDetails) {
        Optional<Producto> productoOptional = productoRepository.findById(id);

        if (productoOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Producto productoExistente = productoOptional.get();
        productoExistente.setNombre(productoDetails.getNombre());
        productoExistente.setPrecio(productoDetails.getPrecio());
        productoExistente.setStock(productoDetails.getStock());
        productoExistente.setDescripcion(productoDetails.getDescripcion());
        productoExistente.setImageLink(productoDetails.getImageLink());

        // Opcional: Permitir reactivar un producto si se edita
        if (productoDetails.getActivo() != null) {
            productoExistente.setActivo(productoDetails.getActivo());
        }

        Producto productoActualizado = productoRepository.save(productoExistente);
        return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
    }

    /**
     * ELIMINAR (Desactivar) un producto de forma segura.
     * 1. Verifica si tiene pedidos pendientes.
     * 2. Si no tiene, hace un "Soft Delete" (activo = false).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        // 1. VERIFICACIÓN DE SEGURIDAD
        // Si hay pedidos pendientes con este producto, PROHIBIDO BORRAR.
        if (pedidoRepository.existsByStatusAndDetalles_Producto_Id("Pendiente", id)) {
            return new ResponseEntity<>("No se puede eliminar: El producto está en un pedido PENDIENTE. Finalice o cancele el pedido primero en la sección de Órdenes.", HttpStatus.CONFLICT);
        }

        // 2. Si no hay pendientes, procedemos con el Soft Delete
        Optional<Producto> productoOpt = productoRepository.findById(id);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();

            // EN LUGAR DE BORRAR, LO DESACTIVAMOS
            producto.setActivo(false);
            productoRepository.save(producto);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND);
        }
    }
}

