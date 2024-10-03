package ar.edu.utn.dds.k3003.controladores;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.Setter;

import javax.persistence.EntityManagerFactory;

public class RutaController {

    private EntityManagerFactory entityManagerFactory;
    private final Fachada fachada;
    //Metrica
    private Counter rutasCounter;

    //instancia StatsDClient es de Datadog, ni idea para que lo metí, lo dejo por si sucede algo en el futuro
    /*
    private static final StatsDClient statsd = new NonBlockingStatsDClient(
            "my.prefix",                  // Prefijo para las métricas
            "localhost",                  // Dirección del agente Datadog
            8125           // Puerto donde escucha el agente
    );
    */
    public RutaController(Fachada fachada, Counter rutasCounter) {
        this.fachada = fachada;
        this.rutasCounter = rutasCounter;
    }

    public void agregar(Context context) {
        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        var rutaDTO = context.bodyAsClass(RutaDTO.class);
        var rutaDTORta = this.fachada.agregar(rutaDTO);

        registry.config().commonTags("app", "metrics-sample");
        Gauge.builder("rutas_agregadas", ()-> (int)(1*1000))
                        .description("rutas de mi app")
                        .strongReference(true)
                        .register(registry);
        new MicrometerPlugin(config -> config.registry = registry);
        rutasCounter.increment();

        context.status(HttpStatus.CREATED);
        context.json(rutaDTORta);

    }
}