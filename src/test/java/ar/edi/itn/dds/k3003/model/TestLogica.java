package ar.edi.itn.dds.k3003.model;
/*
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.fachadas.FachadaHeladerasImp;
import ar.edu.utn.dds.k3003.fachadas.FachadaViandasImp;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.app.Fachada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TestLogica {

    Fachada fachadaLogistica;
    private static final String QR_VIANDA = "123";
    private static final int HELADERA_ORIGEN = 1;
    private static final int HELADERA_DESTINO = 2;

    FachadaViandasImp fachadaViandas;
    FachadaHeladerasImp fachadaHeladeras;


    @BeforeEach
    void setUp(){
        fachadaLogistica = new Fachada();
        fachadaViandas = new FachadaViandasImp();
        fachadaHeladeras = new FachadaHeladerasImp();
        fachadaLogistica.setFachadaViandas(fachadaViandas);
        fachadaLogistica.setFachadaHeladeras(fachadaHeladeras);
    }

    @Test
    @DisplayName("Agregar una ruta")
    void testAgregarRuta(){

      var rutaAgregada = fachadaLogistica.agregar(new RutaDTO(14L, TestLogica.HELADERA_ORIGEN, HELADERA_DESTINO));

      //funca
      assertNotNull(rutaAgregada.getId(), "la ruta una vez agregada deberia tener ID");
    }

    @Test
    @DisplayName("Asignar un traslado")
    void testAsignarTraslado() throws TrasladoNoAsignableException {

        ViandaDTO viandaPrueba =
                new ViandaDTO(
                        QR_VIANDA,
                        LocalDateTime.now(),
                        EstadoViandaEnum.PREPARADA,
                        15L,
                        TestLogica.HELADERA_ORIGEN);

        fachadaLogistica.getFachadaViandas().agregar(viandaPrueba);

        fachadaLogistica.agregar(new RutaDTO(14L, TestLogica.HELADERA_ORIGEN, HELADERA_DESTINO));
        fachadaLogistica.agregar(new RutaDTO(15L, TestLogica.HELADERA_ORIGEN, 3));

     var traslado = new TrasladoDTO(QR_VIANDA, TestLogica.HELADERA_ORIGEN, HELADERA_DESTINO);
     var trasladoDTO = fachadaLogistica.asignarTraslado(traslado);

     //funca
     assertEquals(EstadoTrasladoEnum.ASIGNADO, trasladoDTO.getStatus(), "el estado del traslado tiene que ser asignado, luego de su asignacion");
     assertEquals(14L, trasladoDTO.getColaboradorId(), "no se le asigno el colaborador correcto");
    }

    @Test
    @DisplayName("Asignar un traslado a una vianda que no existe")
    void testAsignarTrasladoAViandaInexistente(){

        fachadaLogistica.agregar(new RutaDTO(14L, HELADERA_ORIGEN, HELADERA_DESTINO));

        var traslado = new TrasladoDTO(QR_VIANDA, HELADERA_ORIGEN, HELADERA_DESTINO);
        //funca
        assertThrows(NoSuchElementException.class,
                () -> fachadaLogistica.asignarTraslado(traslado),
                "El QR no existe, entonces la asignacion falla");
    }

    @Test
    @DisplayName("Asignar un traslado con una ruta que no tiene nadie asignado")
    void testTrasladoNoAsignable() {

        ViandaDTO viandaPrueba =
                new ViandaDTO(
                        QR_VIANDA,
                        LocalDateTime.now(),
                        EstadoViandaEnum.PREPARADA,
                        15L,
                        TestLogica.HELADERA_ORIGEN);

        fachadaLogistica.getFachadaViandas().agregar(viandaPrueba);

        fachadaLogistica.agregar(new RutaDTO(15L, HELADERA_ORIGEN, 3));
        var traslado = new TrasladoDTO(QR_VIANDA, HELADERA_ORIGEN, HELADERA_DESTINO);
        //funca
        assertThrows(TrasladoNoAsignableException.class,
                () -> fachadaLogistica.asignarTraslado(traslado),
                "si no hay rutas posibles, tiene que tirar exception");
    }

    @Test
    @DisplayName("Probar los distintos estados para los traslados")
    void testTrasladoFunca() throws TrasladoNoAsignableException {

        ViandaDTO viandaPrueba =
                new ViandaDTO(
                        QR_VIANDA,
                        LocalDateTime.now(),
                        EstadoViandaEnum.PREPARADA,
                        15L,
                        TestLogica.HELADERA_ORIGEN);

        fachadaLogistica.getFachadaViandas().agregar(viandaPrueba);

        RetiroDTO retiro = new RetiroDTO(QR_VIANDA, "321", HELADERA_ORIGEN);

        fachadaLogistica.agregar(new RutaDTO(15L, HELADERA_ORIGEN, HELADERA_DESTINO));

        var traslado = new TrasladoDTO(QR_VIANDA, HELADERA_ORIGEN, HELADERA_DESTINO);
        TrasladoDTO trasladoDTO = fachadaLogistica.asignarTraslado(traslado);

        fachadaLogistica.trasladoRetirado(trasladoDTO.getId());

        assertEquals(EstadoTrasladoEnum.EN_VIAJE,
                trasladoDTO.getStatus(),
              "El traslado tiene que asignarsele el estado de en viaje una vez se retira la vianda");

        assertEquals(EstadoViandaEnum.EN_TRASLADO,
                fachadaLogistica.getFachadaViandas().buscarXQR(trasladoDTO.getQrVianda()).getEstado(),
                "La vianda debe cambiar de estado una vez es retirada de la heladera");

    }
}
*/