package com.taller.gestion_taller.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vehiculos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_vehiculos_patente_usuario", columnNames = {"patente", "usuario_id"})
})
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String patente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @Column(nullable = false, length = 80)
    private String modelo;

    @Column(nullable = false)
    private Integer anio;

    @Column(length = 30)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "vehiculo")
    private List<OrdenTrabajo> ordenesTrabajo = new ArrayList<>();
}
