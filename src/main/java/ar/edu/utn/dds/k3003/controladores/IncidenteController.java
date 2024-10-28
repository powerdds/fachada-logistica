package ar.edu.utn.dds.k3003.controladores;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.complementos.AlertaDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class IncidenteController {

    private final Fachada fachada;

    public IncidenteController(Fachada fachada){
        this.fachada = fachada;
    }

    public void armarAlerta(Context context){
        var alertaDTO = context.bodyAsClass(AlertaDTO.class);
        context.status(HttpStatus.CREATED);
        context.json(alertaDTO);
    }
}
