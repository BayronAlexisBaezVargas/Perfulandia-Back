package com.example.BackPerfulandia.repository;

import com.example.BackPerfulandia.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método para buscar un Usuario por el campo "email"
    Optional<Usuario> findByEmail(String email);
}
