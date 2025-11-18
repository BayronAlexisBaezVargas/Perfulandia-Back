package com.example.BackPerfulandia.repository;

import com.example.BackPerfulandia.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuario_Id(Long usuarioId);

    boolean existsByStatusAndDetalles_Producto_Id(String status, Long productoId);
}
