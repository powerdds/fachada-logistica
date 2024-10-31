package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.complementos.Retiro;
import ar.edu.utn.dds.k3003.complementos.Ruta;
import ar.edu.utn.dds.k3003.complementos.Traslado;
import ar.edu.utn.dds.k3003.fachadas.FachadaHeladerasImp;
import ar.edu.utn.dds.k3003.fachadas.FachadaViandasImp;
import ar.edu.utn.dds.k3003.repositories.RutaMapper;
import ar.edu.utn.dds.k3003.repositories.RutaRepository;
import ar.edu.utn.dds.k3003.repositories.TrasladoMapper;
import ar.edu.utn.dds.k3003.repositories.TrasladoRepository;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

@Setter
@Getter

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaLogistica {

    private EntityManagerFactory entityManagerFactory;
    private final RutaRepository rutaRepository;
    private final RutaMapper rutaMapper;
    private final TrasladoRepository trasladoRepository;
    private final TrasladoMapper trasladoMapper;
    private FachadaViandas fachadaViandas;
    private FachadaHeladeras fachadaHeladeras;


    public Fachada(EntityManagerFactory entityManagerFactory){
        this.entityManagerFactory = entityManagerFactory;
        this.rutaRepository = new RutaRepository();
        this.rutaMapper = new RutaMapper();
        this.trasladoMapper = new TrasladoMapper();
        this.trasladoRepository = new TrasladoRepository();
    }
    public Fachada(){
        this.rutaRepository = new RutaRepository();
        this.rutaMapper = new RutaMapper();
        this.trasladoMapper = new TrasladoMapper();
        this.trasladoRepository = new TrasladoRepository();
    }




    @Override
    public RutaDTO agregar(RutaDTO rutaDTO){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        rutaRepository.setEntityManager(entityManager);
        rutaRepository.getEntityManager().getTransaction().begin();
        Ruta ruta = new Ruta(rutaDTO.getColaboradorId(), rutaDTO.getHeladeraIdOrigen(), rutaDTO.getHeladeraIdDestino());
        ruta = this.rutaRepository.save(ruta);
        rutaRepository.getEntityManager().getTransaction().commit();
        rutaRepository.getEntityManager().close();
        return rutaMapper.map(ruta);
    }

    @Override
    public TrasladoDTO buscarXId (Long id){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        trasladoRepository.setEntityManager(entityManager);
        trasladoRepository.getEntityManager().getTransaction().begin();
        Traslado trasladoBuscado =  this.trasladoRepository.findById(id);
        trasladoRepository.getEntityManager().getTransaction().commit();
        trasladoRepository.getEntityManager().close();
        return trasladoMapper.map(trasladoBuscado);
    }

    @Override
    public TrasladoDTO asignarTraslado(TrasladoDTO trasladoDTO) throws TrasladoNoAsignableException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        rutaRepository.setEntityManager(entityManager);
        trasladoRepository.setEntityManager(entityManager);

        trasladoRepository.getEntityManager().getTransaction().begin();
        fachadaViandas.buscarXQR(trasladoDTO.getQrVianda()); //Tirar 404

        List<Ruta> rutasPosibles = this.rutaRepository.findByHeladeras(trasladoDTO.getHeladeraOrigen(), trasladoDTO.getHeladeraDestino());

        if(rutasPosibles.isEmpty()){
            entityManager.getTransaction().rollback();
            entityManager.close();

            throw new TrasladoNoAsignableException("El traslado no es asignable, no tiene rutas posibles.");
        }


        Traslado trasladoAsignado = new Traslado(trasladoDTO.getQrVianda(), rutasPosibles.get(0), EstadoTrasladoEnum.ASIGNADO, trasladoDTO.getFechaTraslado());

        trasladoAsignado = this.trasladoRepository.save(trasladoAsignado);

        trasladoRepository.getEntityManager().getTransaction().commit();
        trasladoRepository.getEntityManager().close();
        rutaRepository.getEntityManager().close();

        return trasladoMapper.map(trasladoAsignado);
    }
    @Override
    public List<TrasladoDTO> trasladosDeColaborador(Long colaboradorId, Integer mes, Integer anio){

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        trasladoRepository.setEntityManager(entityManager);
        trasladoRepository.getEntityManager().getTransaction().begin();

        List<Traslado> trasladosDeColaborador = trasladoRepository.findByColaborador(colaboradorId);

        List<Traslado> trasladosDeColaboradorPedidos = trasladosDeColaborador.stream().filter(x -> x.getFechaTraslado().getMonthValue() == mes
                                                && x.getFechaTraslado().getYear() == anio).toList();

        trasladoRepository.getEntityManager().getTransaction().commit();
        trasladoRepository.getEntityManager().close();
        List<TrasladoDTO> trasladosDTOColaborador = new ArrayList<>();

            for(Traslado trasladoColaborador : trasladosDeColaboradorPedidos){
                TrasladoDTO trasladoDTO = new TrasladoDTO(trasladoColaborador.getQrVianda(),
                                            trasladoColaborador.getEstado(),
                                            trasladoColaborador.getFechaTraslado(),
                                            trasladoColaborador.getRuta().getHeladeraIdOrigen(),
                                            trasladoColaborador.getRuta().getHeladeraIdDestino());

                trasladosDTOColaborador.add(trasladoDTO);
            }

        return trasladosDTOColaborador;
    }
    @Override
    public void trasladoRetirado(Long trasladoId){
        //EntityManager entityManager = entityManagerFactory.createEntityManager();
        //trasladoRepository.setEntityManager(entityManager);
        //trasladoRepository.getEntityManager().getTransaction().begin();
        TrasladoDTO trasladoBuscado = this.buscarXId(trasladoId);

        Ruta rutaDeTraslado = new Ruta(trasladoBuscado.getColaboradorId(), trasladoBuscado.getHeladeraOrigen(), trasladoBuscado.getHeladeraDestino());

        RetiroDTO retiroDTO = new RetiroDTO(trasladoBuscado.getQrVianda(), "321", trasladoBuscado.getHeladeraOrigen());

        fachadaHeladeras.retirar(retiroDTO);

        //Esto de viandas capaz va, capaz no, a checkear. LO HACE HELADERAS.
        //fachadaViandas.modificarEstado(trasladoBuscado.getQrVianda(), EstadoViandaEnum.EN_TRASLADO);

        //trasladoRepository.getEntityManager().getTransaction().commit();
        //trasladoRepository.getEntityManager().close();

        this.modificarEstadoTraslado(trasladoId, EstadoTrasladoEnum.EN_VIAJE);
        /*
        trasladoRepository.save(new Traslado(trasladoBuscado.getQrVianda(),
                                rutaDeTraslado,
                                EstadoTrasladoEnum.EN_VIAJE,
                                trasladoBuscado.getFechaTraslado()));
        */

    }

    @Override
    public void trasladoDepositado(Long trasladoId){
        //EntityManager entityManager = entityManagerFactory.createEntityManager();
        //trasladoRepository.setEntityManager(entityManager);
        //trasladoRepository.getEntityManager().getTransaction().begin();
        TrasladoDTO trasladoTerminado = this.buscarXId(trasladoId);

        if(!trasladoTerminado.getStatus().equals(EstadoTrasladoEnum.EN_VIAJE)){
           // trasladoRepository.getEntityManager().getTransaction().rollback();
           // trasladoRepository.getEntityManager().close();
            throw new NoSuchElementException("La vianda con qr" + trasladoTerminado.getQrVianda() +" aún no fue retirada");
        }

        Ruta rutaDeTraslado = new Ruta(trasladoTerminado.getColaboradorId(), trasladoTerminado.getHeladeraOrigen(), trasladoTerminado.getHeladeraDestino());

        fachadaHeladeras.depositar(trasladoTerminado.getHeladeraDestino(), trasladoTerminado.getQrVianda());

        //Esto como en traslado retirado, podria ir, o no, a checkear. Lo hace HELADERAS.
        //fachadaViandas.modificarEstado(trasladoTerminado.getQrVianda(),EstadoViandaEnum.DEPOSITADA);

        fachadaViandas.modificarHeladera(trasladoTerminado.getQrVianda(),trasladoTerminado.getHeladeraDestino());
        //trasladoRepository.getEntityManager().getTransaction().commit();
        //trasladoRepository.getEntityManager().close();
        this.modificarEstadoTraslado(trasladoId, EstadoTrasladoEnum.ENTREGADO);
        /*
        trasladoRepository.save(new Traslado(trasladoTerminado.getQrVianda(),
                                            rutaDeTraslado,
                                            EstadoTrasladoEnum.ENTREGADO,
                                            trasladoTerminado.getFechaTraslado()));
    */
    }


    public void modificarEstadoTraslado(Long trasladoId, EstadoTrasladoEnum nuevoEstado) throws NoSuchElementException{
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        trasladoRepository.setEntityManager(entityManager);
        trasladoRepository.getEntityManager().getTransaction().begin();
        trasladoRepository.modificarEstadoTraslado(trasladoId, nuevoEstado);
        trasladoRepository.getEntityManager().getTransaction().commit();
        trasladoRepository.getEntityManager().close();
    }
    @Override
    public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras){
        this.fachadaHeladeras = fachadaHeladeras;
    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas){
        this.fachadaViandas = fachadaViandas;
    }

}
