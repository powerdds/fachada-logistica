package ar.edu.utn.dds.k3003.controladores;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.complementos.Traslado;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class TrasladoController {

    private final Fachada fachada;

    //metrica
    private Counter trasladosAsignadosCounter;

    private Counter cambioDeEstadoCounter;

    public TrasladoController(Fachada fachada, Counter trasladosAsignadosCounter, Counter cambioDeEstadoCounter) {

        this.fachada = fachada;
        this.trasladosAsignadosCounter = trasladosAsignadosCounter;
        this.cambioDeEstadoCounter = cambioDeEstadoCounter;
    }

    public void asignar(Context context) {
        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        try {
            var trasladoDTO = this.fachada.asignarTraslado(context.bodyAsClass(TrasladoDTO.class));

            registry.config().commonTags("app", "metrics-sample");
            Gauge.builder("traslados-asignados", ()-> (int)(1*1000))
                    .description("traslados asignados para mover las viandas")
                    .strongReference(true)
                    .register(registry);
            new MicrometerPlugin(config -> config.registry = registry);
            trasladosAsignadosCounter.increment();

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
            context.status(HttpStatus.OK);
            context.json(trasladoDTO);
        } catch (NoSuchElementException ex) {
            context.status(404).result("Traslado " + id + "no encontrado" + ex.getMessage());
        }
    }

    public void obtenerTodos(Context context){

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
        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        var id = context.pathParamAsClass("id",Long.class).get();
        EstadoTrasladoEnum nuevoEstado = context.bodyAsClass(EstadoTrasladoEnum.class);
        fachada.modificarEstadoTraslado(id, nuevoEstado);

        registry.config().commonTags("app", "metrics-sample");
        Gauge.builder("cambios-estado-traslado", ()-> (int)(1*1000))
                .description("cambios de estado registrados en los traslados")
                .strongReference(true)
                .register(registry);
        new MicrometerPlugin(config -> config.registry = registry);

        trasladosAsignadosCounter.increment();
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