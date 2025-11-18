package com.example.BackPerfulandia.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usaremos un ID de pedido legible para el cliente, como el #PF-0815
    @Column(name = "numero_pedido", unique = true, nullable = false)
    private String numeroPedido;

    @ManyToOne(fetch = FetchType.EAGER) // <-- CORREGIDO
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private String status; // Ej: "Pendiente", "Enviado", "Completado"

    @Column(name = "direccion_envio", length = 1000)
    private String direccionEnvio;

    // Relación: UN pedido tiene MUCHOS detalles (productos)
    // CascadeType.ALL: Si guardo un pedido, guarda sus detalles. Si borro un pedido, borra sus detalles.
    // orphanRemoval = true: Si quito un detalle de la lista, se borra de la BD.
    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER // Cargar los detalles junto con el pedido
    )
    private List<DetallePedido> detalles = new ArrayList<>();

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
        // Asignamos este pedido a cada detalle
        for (DetallePedido detalle : detalles) {
            detalle.setPedido(this);
        }
    }
}
