package ar.edu.utn.dds.k3003.complementos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor

@Entity
@Table(name = "ruta")

public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long colaboradorId;
    @Column
    private Integer heladeraIdOrigen;
    @Column
    private Integer heladeraIdDestino;
    @Column
    private LocalDateTime fechaCreacion;
    @Column
    private Boolean activo;

    protected Ruta(){
        super();
    }

    public Ruta(Long colaboradorId, Integer heladeraIdOrigen, Integer heladeraIdDestino){
        this.colaboradorId = colaboradorId;
        this.heladeraIdOrigen = heladeraIdOrigen;
        this.heladeraIdDestino = heladeraIdDestino;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;

    }

}
