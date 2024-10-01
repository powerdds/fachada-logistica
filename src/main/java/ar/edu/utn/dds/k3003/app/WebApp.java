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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebApp {
//nuevo
    public static EntityManagerFactory entityManagerFactory;
    public static void main(String[] args) {
        //E3
        startEntityManagerFactory();


        //e4
        final var metricsUtils = new DDMetricsUtils("logistica");
        final var registry = metricsUtils.getRegistry();

        //Metricas
        final var myGauge = registry.gauge("dds.unGauge", new AtomicInteger(0));

        //Config
        final var micrometerPlugin = new MicrometerPlugin(config -> config.registry = registry);

        var env = System.getenv();
        var objectMapper = createObjectMapper();
        var fachada = new Fachada(entityManagerFactory);
        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));

        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                configureObjectMapper(mapper);
            }));
            config.registerPlugin(micrometerPlugin);
        }).start(port);

        myGauge.set(100);
        registry.counter("dds.transferencias","status","ok").increment();

        var rutaController = new RutaController(fachada);
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