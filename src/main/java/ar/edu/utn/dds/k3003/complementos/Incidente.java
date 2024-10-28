package ar.edu.utn.dds.k3003.complementos;

import java.time.LocalDateTime;

public class Incidente {
    private Long heladeraId;

    private LocalDateTime fechaOcurrencia;

    private TipoIncidente tipoIncidente;

    public Incidente(Long heladeraId, TipoIncidente tipoIncidente){
        this.heladeraId = heladeraId;
        this.fechaOcurrencia = LocalDateTime.now();
        this.tipoIncidente = tipoIncidente;
    }
}
