package ar.edu.utn.dds.k3003.clientes;

import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class ColaboradoresProxy {

    private final String endpoint;
    private final ColaboradoresRetrofitClient service;

    public ColaboradoresProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.get("URL_COLABORADORES");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(ColaboradoresRetrofitClient.class);
    }

    @SneakyThrows
    public void asignarTraslado(Long id, TrasladoDTO trasladoDTO) throws NoSuchElementException {

        Response<Void> execute = service.reportarTraslado(id, trasladoDTO).execute();

        if (execute.isSuccessful()) {
            System.out.println("Se a enviado el reporte de traslado de forma exitosa!");
        } else {
            throw new RuntimeException("Error conectandose con el componente colaboradores");
        }
    }
}