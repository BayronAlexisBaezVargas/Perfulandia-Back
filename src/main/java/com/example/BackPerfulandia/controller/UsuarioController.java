package com.example.BackPerfulandia.controller;

import com.example.BackPerfulandia.model.Usuario;
import com.example.BackPerfulandia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Endpoint para OBTENER todos los usuarios (para AdminCustomers.js)
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        // Ocultar contraseñas en la lista
        usuarios.forEach(user -> user.setPassword(null));
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    /**
     * Endpoint para OBTENER un usuario por ID (para ver un perfil)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        return usuarioOptional
                .map(usuario -> {
                    usuario.setPassword(null); // Ocultar contraseña
                    return new ResponseEntity<>(usuario, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Endpoint para ACTUALIZAR un perfil de usuario (para Perfil.js)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Usuario usuarioExistente = usuarioOptional.get();

        // Actualizar solo los campos del perfil
        usuarioExistente.setName(usuarioDetails.getName());
        usuarioExistente.setRut(usuarioDetails.getRut());
        usuarioExistente.setAddress(usuarioDetails.getAddress());
        usuarioExistente.setPhone(usuarioDetails.getPhone());

        // Nota: No permitimos cambiar email, password o role desde este endpoint
        // para mantener la seguridad.

        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
        usuarioActualizado.setPassword(null); // Ocultar contraseña en la respuesta

        return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
    }

    /**
     * Endpoint para ELIMINAR un usuario (para AdminCustomers.js)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
