package ar.edu.utn.dds.k3003.fachadas;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@AllArgsConstructor

public class FachadaViandasImp {
    private Collection<ViandaDTO> viandas;

    public FachadaViandasImp(){
        this.viandas = new ArrayList<>();
    }
    public ViandaDTO agregar(ViandaDTO viandaDTO){
        this.viandas.add(viandaDTO);
        return viandaDTO;
    }

    public ViandaDTO buscarXQR(String qr){
        Optional<ViandaDTO> first = this.viandas.stream().filter(x -> x.getCodigoQR().equals(qr)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay una ruta de qr: %s", qr)
        ));
    }


    public void modificarEstado(String qrVianda, EstadoViandaEnum estadoViandaEnum) {
        Vianda viandaCambiada = new Vianda(qrVianda, LocalDateTime.now(), estadoViandaEnum, 1L, 1);
    }
}
