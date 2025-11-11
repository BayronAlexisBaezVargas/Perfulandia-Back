package com.example.BackPerfulandia.controller;

import com.example.BackPerfulandia.model.Producto;
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

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> productoOptional = productoRepository.findById(id);

        // Verifica si el producto existe y devuelve 200 OK o 404 NOT FOUND
        return productoOptional
                .map(producto -> new ResponseEntity<>(producto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        // Aseguramos que el ID sea nulo para que JPA sepa que es una CREACIÓN
        producto.setId(null);
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

        Producto productoActualizado = productoRepository.save(productoExistente);
        return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProducto(@PathVariable Long id) {
        try {
            productoRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // Esto puede pasar si el ID no existe
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
