package com.taller.gestion_taller.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ordenes_trabajo")
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant fechaIngreso;

    private Instant fechaEntrega;

    private Long kilometraje;

    @Column(nullable = false, length = 1000)
    private String problemaInformado;

    @Column(length = 1000)
    private String diagnostico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoOrden estado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pagado = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrdenTrabajo> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenOrden> imagenes = new ArrayList<>();
}
