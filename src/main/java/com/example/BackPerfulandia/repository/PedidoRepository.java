package com.example.BackPerfulandia.repository;

import com.example.BackPerfulandia.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Spring Data JPA creará automáticamente este método.
     * Buscará todos los Pedidos que coincidan con el ID de usuario proporcionado.
     * Usaremos "findByUsuario_Id" para buscar por el campo "id" dentro del objeto "usuario".
     */
    List<Pedido> findByUsuario_Id(Long usuarioId);
}
