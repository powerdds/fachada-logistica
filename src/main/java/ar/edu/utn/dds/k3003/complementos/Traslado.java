package ar.edu.utn.dds.k3003.complementos;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor

@Entity
@Table(name = "traslado")

public class Traslado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String qrVianda;
    @OneToOne
    @JoinColumn(name = "ruta_id", referencedColumnName = "id")
    private Ruta ruta;
    @Column
    @Enumerated(EnumType.STRING)
    private EstadoTrasladoEnum estado;
    @Column
    private LocalDateTime fechaCreacion;
    @Column
    private LocalDateTime fechaTraslado;
    //Rompia si ponia un constructor vacio, QUE NECESITO PARA PERSISTIR, dado que habia atributos con final. Eran fechaCreacion/Traslado, Ruta y QRVianda
    @Column
    private Long colaboradorId;
    protected Traslado(){
        super();
    }
    public Traslado(String qrVianda, Ruta ruta, EstadoTrasladoEnum estado, LocalDateTime fechaTraslado) {
        this.qrVianda = qrVianda;
        this.ruta = ruta;
        this.estado = estado;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaTraslado = fechaTraslado;
        this.colaboradorId = ruta.getColaboradorId();
    }


}