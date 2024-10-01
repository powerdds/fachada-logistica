package ar.edu.utn.dds.k3003.fachadas;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class Vianda {
    private String codigoQR;
    private LocalDateTime fechaElaboracion;
    private EstadoViandaEnum estado;
    private Long colaboradorId;
    private Integer heladeraOrigen;

}
