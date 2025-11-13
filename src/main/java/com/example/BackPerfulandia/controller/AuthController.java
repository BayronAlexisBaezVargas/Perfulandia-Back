package com.example.BackPerfulandia.controller;

import com.example.BackPerfulandia.model.Usuario;
import com.example.BackPerfulandia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario usuario) {

        // 1. Verificar si el email ya existe
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return new ResponseEntity<>("El correo electrónico ya está registrado.", HttpStatus.CONFLICT);
        }

        // 2. Hashear la contraseña ANTES de guardarla
        String hashedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(hashedPassword);

        // 3. NUEVA LÓGICA: Asignar rol de "admin" a los correos especiales
        String email = usuario.getEmail();
        if ("marcelo.c@duoc.cl".equalsIgnoreCase(email) || "bay.baez@duocuc.cl".equalsIgnoreCase(email)) {
            usuario.setRole("admin");
        } else {
            usuario.setRole("user");
        }

        // 4. Guardar el nuevo usuario
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        nuevoUsuario.setPassword(null); // Ocultar contraseña en la respuesta

        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isEmpty()) {
            return new ResponseEntity<>("Credenciales inválidas.", HttpStatus.UNAUTHORIZED);
        }

        Usuario usuario = usuarioOptional.get();

        if (passwordEncoder.matches(password, usuario.getPassword())) {
            usuario.setPassword(null); // Ocultar contraseña
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Credenciales inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }
}