package ar.edu.utn.dds.k3003.clientes;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.*;

public class HeladerasProxy implements FachadaHeladeras {

    private final String endpoint;
    private final HeladerasRetrofitClient service;

    public HeladerasProxy(ObjectMapper objectMapper){
        var env = System.getenv();

        this.endpoint = env.getOrDefault("URL_HELADERAS", "http://localhost:8082/");

        var retrofit = new Retrofit.Builder().baseUrl(this.endpoint).addConverterFactory(JacksonConverterFactory.create(objectMapper)).build();

        this.service = retrofit.create(HeladerasRetrofitClient.class);
    }
    @Override
    public HeladeraDTO agregar(HeladeraDTO heladeraDTO){
        return null;
    }

    @SneakyThrows
    @Override
    public void  depositar(Integer valor, String cadena) throws NoSuchElementException {
        ViandaDTO viandaDTO = new ViandaDTO(cadena,null,null,null,valor);

        boolean exito = false;
        Response<Void> execute = service.depositar(viandaDTO).execute();

        if(execute.isSuccessful()){
            execute.body();
            exito = true;
        }
        if (execute.code() == HttpStatus.BAD_REQUEST.getCode()) {
            throw new NoSuchElementException("No se pudo depositar la vianda " + cadena);
        }
    }

    @Override
    public Integer cantidadViandas(Integer valor) throws NoSuchElementException {
        return null;
    }

    @SneakyThrows
    @Override
    public void retirar(RetiroDTO retiroDTO) throws NoSuchElementException {
        Response<Void> execute = service.retirar(retiroDTO).execute();
        boolean exito = false;
        if(execute.isSuccessful()){
            execute.body();
            exito = true;
        }
        if (execute.code() == HttpStatus.BAD_REQUEST.getCode()) {
            throw new NoSuchElementException("No se pudo retirar la vianda " + retiroDTO.getQrVianda());
        }
    }

    @Override
    public void temperatura(TemperaturaDTO temperaturaDTO){}

    @Override
    public List<TemperaturaDTO> obtenerTemperaturas(Integer valor){
        return null;
    }
    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas){
    }



}
