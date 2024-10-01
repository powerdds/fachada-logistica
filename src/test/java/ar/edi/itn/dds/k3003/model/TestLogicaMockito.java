package ar.edi.itn.dds.k3003.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestLogicaMockito implements TestTP<Fachada>{

    private static final String QR_VIANDA = "1";
    private static final int HELADERA_ORIGEN = 1;
    private static final int HELADERA_DESTINO = 2;

    Fachada instancia;
    @Mock FachadaViandas fachadaViandas;
    @Mock FachadaHeladeras fachadaHeladeras;

    @SneakyThrows
    @BeforeEach
    void setUp() {

        instancia = this.instance();
        instancia.setHeladerasProxy(fachadaHeladeras);
        instancia.setViandasProxy(fachadaViandas);

    }
    @Test
    @DisplayName("Agregado de ruta")
    void testAgregarUnaRuta(){
        var rutaAAgregar = new RutaDTO(1L, HELADERA_ORIGEN, HELADERA_DESTINO);
        RutaDTO rutaAgregada = instancia.agregar(rutaAAgregar);
        assertNotNull(rutaAgregada.getId(), "una vez agregada una ruta, tiene que tener un ID");
    }

    @Test
    @DisplayName("Asignar traslado")
    void testAsignarTraslado() throws TrasladoNoAsignableException {
        var rutaAAgregar = new RutaDTO(1L, HELADERA_ORIGEN, HELADERA_DESTINO);

        instancia.agregar(rutaAAgregar);

        ViandaDTO viandaPrueba = new ViandaDTO(QR_VIANDA,
                                                LocalDateTime.now(),
                                                EstadoViandaEnum.PREPARADA,
                                                1L,
                                                HELADERA_ORIGEN);

        when(fachadaViandas.buscarXQR(QR_VIANDA)).thenReturn(viandaPrueba);

        var trasladoNoAsignado = new TrasladoDTO(QR_VIANDA, HELADERA_ORIGEN, HELADERA_DESTINO);

        var trasladoAsignado = instancia.asignarTraslado(trasladoNoAsignado);

        assertEquals(EstadoTrasladoEnum.ASIGNADO, trasladoAsignado.getStatus(), "Una vez se le asigna un traslado a un colaborador, se actualiza su estado a ASIGNADO");
    }

    @Test
    @DisplayName("Conseguir traslados de un colaborador")
    void testTrasladosDeColaborador() throws TrasladoNoAsignableException {
        TrasladoDTO trasladoDTO1 = new TrasladoDTO(QR_VIANDA,EstadoTrasladoEnum.ASIGNADO,LocalDateTime.of(2002,Month.MAY,5,2,1),HELADERA_ORIGEN,HELADERA_DESTINO);
        TrasladoDTO trasladoDTO2 = new TrasladoDTO("321",EstadoTrasladoEnum.ASIGNADO,LocalDateTime.of(2002,Month.MAY,6,2,1),HELADERA_ORIGEN,3);
        List<TrasladoDTO> trasladosDTOs = new ArrayList<>();
        trasladosDTOs.add(trasladoDTO1);
        trasladosDTOs.add(trasladoDTO2);

        ViandaDTO vianda1 =
                new ViandaDTO(
                        QR_VIANDA,
                        LocalDateTime.now(),
                        EstadoViandaEnum.PREPARADA,
                        14L,
                        TestLogicaMockito.HELADERA_ORIGEN);

        ViandaDTO vianda2 =
                new ViandaDTO(
                        "321",
                        LocalDateTime.now(),
                        EstadoViandaEnum.PREPARADA,
                        14L,
                        TestLogicaMockito.HELADERA_ORIGEN);

        when(fachadaViandas.buscarXQR(QR_VIANDA)).thenReturn(vianda1);
        when(fachadaViandas.buscarXQR("321")).thenReturn(vianda2);

        instancia.agregar(new RutaDTO(14L, HELADERA_ORIGEN, HELADERA_DESTINO));
        instancia.agregar(new RutaDTO(14L, HELADERA_ORIGEN,3));

        instancia.asignarTraslado(trasladoDTO1);
        instancia.asignarTraslado(trasladoDTO2);

        assertEquals(trasladosDTOs,
                instancia.trasladosDeColaborador(14L, 5, 2002));
    }

    @Test
    @DisplayName("Retirar una vianda, tambien prueba el buscarXId")
    void testTrasladoRetirado() throws TrasladoNoAsignableException {
        ViandaDTO viandaPrueba = new ViandaDTO(QR_VIANDA,
                LocalDateTime.now(),
                EstadoViandaEnum.DEPOSITADA,
                1L,
                HELADERA_ORIGEN);

        when(fachadaViandas.buscarXQR(QR_VIANDA)).thenReturn(viandaPrueba);

        var rutaAAgregar = new RutaDTO(1L, HELADERA_ORIGEN, HELADERA_DESTINO);

        instancia.agregar(rutaAAgregar);

        var trasladoNoAsignado = new TrasladoDTO(QR_VIANDA, HELADERA_ORIGEN, HELADERA_DESTINO);

        var trasladoAsignado = instancia.asignarTraslado(trasladoNoAsignado);

        instancia.trasladoRetirado(trasladoAsignado.getId());

        verify(fachadaViandas).modificarEstado(QR_VIANDA,EstadoViandaEnum.EN_TRASLADO);
    }

    @Test
    @DisplayName("Depositar una vianda, tambien prueba el buscarXId")
    void testTrasladoDepositado() throws TrasladoNoAsignableException {
        ViandaDTO viandaPrueba = new ViandaDTO(QR_VIANDA,
                LocalDateTime.now(),
                EstadoViandaEnum.DEPOSITADA,
                1L,
                HELADERA_ORIGEN);

        when(fachadaViandas.buscarXQR(QR_VIANDA)).thenReturn(viandaPrueba);

        var rutaAAgregar = new RutaDTO(1L, HELADERA_ORIGEN, HELADERA_DESTINO);

        instancia.agregar(rutaAAgregar);

        var trasladoNoAsignado = new TrasladoDTO(QR_VIANDA, HELADERA_ORIGEN, HELADERA_DESTINO);

        var trasladoAsignado = instancia.asignarTraslado(trasladoNoAsignado);

        instancia.trasladoRetirado(trasladoAsignado.getId());
        instancia.trasladoDepositado(trasladoAsignado.getId());

        verify(fachadaViandas).modificarEstado(QR_VIANDA,EstadoViandaEnum.DEPOSITADA);
    }

    @Override
    public String paquete() {
        return PAQUETE_BASE + ".model.TestLogicaMockito";
    }

    @Override
    public Class<Fachada> clase() {
        return Fachada.class;
    }

}
