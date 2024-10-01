package ar.edu.utn.dds.k3003.complementos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor


public class Retiro {

    private Long id;
    private String qrVianda;
    private String tarjeta;
    private LocalDateTime fechaDeRetiro;
    private Integer heladeraId;

    public Retiro(String qrVianda, LocalDateTime fechaDeRetiro, Integer heladeraId){
        this.qrVianda = qrVianda;
        this.fechaDeRetiro = fechaDeRetiro;
        this.heladeraId = heladeraId;
    }
}
