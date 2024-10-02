package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clientes.HeladerasProxy;
import ar.edu.utn.dds.k3003.clientes.ViandasProxy;
import ar.edu.utn.dds.k3003.controladores.DBController;
import ar.edu.utn.dds.k3003.controladores.RutaController;
import ar.edu.utn.dds.k3003.controladores.TrasladoController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

//E4
import java.util.concurrent.atomic.AtomicInteger;
import io.javalin.http.HttpStatus;
import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebApp {
//nuevo
    public static EntityManagerFactory entityManagerFactory;
    public static final String TOKEN = "LogisticaToken";
    public static void main(String[] args) {
        //E3
        startEntityManagerFactory();
        //E4 ahora grafana
        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        //
        var env = System.getenv();
        var objectMapper = createObjectMapper();
        var fachada = new Fachada(entityManagerFactory);

        //e4
        registry.config().commonTags("app", "metrics-sample");
        //metricas jvm
        try (var jvmGcMetrics = new JvmGcMetrics();
             var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()){
            jvmGcMetrics.bindTo(registry);
            jvmHeapPressureMetrics.bindTo(registry);
        }
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);

        //metricas custom
        Counter rutasCounter = Counter.builder("rutas_creadas")
                        .description("Total de rutas validas creadas")
                        .register(registry);

        //Se setea el registro dentro de la config de Micrometer
        final var micrometerPlugin = new MicrometerPlugin(config -> config.registry = registry);

        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));

        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                configureObjectMapper(mapper);
            }));
            config.registerPlugin(micrometerPlugin); //esto para la webapp
        }).start(port);

        var rutaController = new RutaController(fachada, rutasCounter);
        var trasladosController = new TrasladoController(fachada);
        var dbController = new DBController(fachada);

        app.post("/rutas", rutaController::agregar);
        app.post("/traslados", trasladosController::asignar);
        app.get("/traslados/search/findByColaboradorId", trasladosController::trasladosColaborador);
        app.get("/traslados/{id}", trasladosController::obtener);
        app.patch("/traslados/{id}", trasladosController::cambiarEstado);
       app.delete("/cleanup" , dbController::eliminarDB);
       app.post("/retirarTraslado/{id}" , trasladosController::retirarTraslado);
       app.post("/depositarTraslado/{id}" , trasladosController::depositarTraslado);
       //endpoint para grafana
        app.get("/metrics",
                ctx -> {
                    // chequear el header de authorization y chequear el token bearer
                    // configurado
                    var auth = ctx.header("Authorization");

                    if (auth != null && auth.intern() == "Bearer " + TOKEN) {
                        ctx.contentType("text/plain; version=0.0.4")
                                .result(registry.scrape());
                    } else {
                        // si el token no es el apropiado, devolver error,
                        // desautorizado
                        // este paso es necesario para que Grafana online
                        // permita el acceso
                        ctx.status(401).json("unauthorized access");
                    }
                });
    }


    //nuevo metodo
    public static void startEntityManagerFactory(){
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        String[] keys = new String[] { "javax.persistence.jdbc.url", "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.password", "javax.persistence.jdbc.driver", "hibernate.hbm2ddl.auto",
                "hibernate.connection.pool_size", "hibernate.show_sql" };
        for (String key : keys) {
            if (env.containsKey(key)) {
                String value = env.get(key);
                configOverrides.put(key, value);
            }
        }
        entityManagerFactory = Persistence.createEntityManagerFactory("db", configOverrides);

    }

    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }

    public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
    }
}