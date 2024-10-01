package ar.edu.utn.dds.k3003.controladores;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.complementos.Traslado;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class TrasladoController {

    private final Fachada fachada;

    public TrasladoController(Fachada fachada) {
        this.fachada = fachada;
    }

    public void asignar(Context context) {
        try {
            var trasladoDTO = this.fachada.asignarTraslado(context.bodyAsClass(TrasladoDTO.class));
            context.json(trasladoDTO);
        } catch (TrasladoNoAsignableException | NoSuchElementException e) {
            context.result(e.getLocalizedMessage());
            context.status(HttpStatus.BAD_REQUEST);
        }
    }

    public void obtener(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var trasladoDTO = this.fachada.buscarXId(id);
            context.json(trasladoDTO);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void trasladosColaborador(Context context){
        //var id = context.pathParamAsClass("colaboradorId", Long.class).get();
        var id = context.queryParamAsClass("colaboradorId", Long.class).get();
        try {
            var listaDeTraslados = this.fachada.trasladosDeColaborador(id, LocalDateTime.now().getMonthValue(), LocalDateTime.now().getYear());
            context.json(listaDeTraslados);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    /*
    public void cambiarEstado(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var trasladoDTO = this.fachada.buscarXId(id);
            var trasladoDTOnuevo = new TrasladoDTO(trasladoDTO.getQrVianda(),context.bodyAsClass(EstadoTrasladoEnum.class), trasladoDTO.getFechaTraslado(), trasladoDTO.getHeladeraOrigen(), trasladoDTO.getHeladeraDestino());
            context.json(trasladoDTOnuevo);
        } catch (NoSuchElementException ex){
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

     */

    public void cambiarEstado(Context context){
        var id = context.pathParamAsClass("id",Long.class).get();
        EstadoTrasladoEnum nuevoEstado = context.bodyAsClass(EstadoTrasladoEnum.class);
        fachada.modificarEstadoTraslado(id, nuevoEstado);
        context.result("Estado del traslado modificado");
    }

    public void retirarTraslado(Context context) throws  Exception{
        try{
            var id = context.pathParamAsClass("id", Long.class).get();
            fachada.trasladoRetirado(id);
            context.result("La vianda se retiró correctamente");
        }catch (NoSuchElementException e){
            throw new BadRequestResponse(e.getMessage());
        }
    }

    public void depositarTraslado(Context context) throws  Exception{
        try{
            var id = context.pathParamAsClass("id", Long.class).get();
            fachada.trasladoDepositado(id);
            context.result("La vianda se depositó correctamente");
        }catch (NoSuchElementException e){
            throw new BadRequestResponse(e.getMessage());
        }
    }
}