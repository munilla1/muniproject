package com.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq_gen")
    @SequenceGenerator(name = "producto_seq_gen", sequenceName = "producto_seq", allocationSize = 1)
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private boolean disponible;
    private String rutaArchivo;
    private String rutaImagen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Relación con el usuario que publicó el producto
    private Usuario usuario;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentEntity> pagos;
}