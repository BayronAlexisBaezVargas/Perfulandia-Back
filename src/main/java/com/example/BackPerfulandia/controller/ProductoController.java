package com.example.BackPerfulandia.controller;

import com.example.BackPerfulandia.model.Producto;
import com.example.BackPerfulandia.repository.ProductoRepository;
import com.example.BackPerfulandia.repository.PedidoRepository;
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

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> productoOptional = productoRepository.findById(id);
        return productoOptional
                .map(producto -> new ResponseEntity<>(producto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        producto.setId(null);
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
        Producto nuevoProducto = productoRepository.save(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

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

        if (productoDetails.getActivo() != null) {
            productoExistente.setActivo(productoDetails.getActivo());
        }

        Producto productoActualizado = productoRepository.save(productoExistente);
        return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        // 1. VERIFICACIÓN DE PENDIENTES
        if (pedidoRepository.existsByStatusAndDetalles_Producto_Id("Pendiente", id)) {
            return new ResponseEntity<>("No se puede eliminar: El producto está en un pedido PENDIENTE. Finalice o cancele el pedido primero en la sección de Órdenes.", HttpStatus.CONFLICT);
        }

        // 2. Soft Delete
        Optional<Producto> productoOpt = productoRepository.findById(id);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setActivo(false);
            productoRepository.save(producto);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND);
        }
    }
}