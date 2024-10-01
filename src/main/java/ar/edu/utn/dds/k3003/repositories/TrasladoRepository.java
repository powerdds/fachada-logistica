package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.complementos.Ruta;
import ar.edu.utn.dds.k3003.complementos.Traslado;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
public class TrasladoRepository {

    private EntityManager entityManager;

    private static AtomicLong seqId = new AtomicLong();
    private Collection<Traslado> traslados;

    public TrasladoRepository(EntityManager entityManager){
        super();
        this.entityManager = entityManager;
    }
    public TrasladoRepository(){

        //this.traslados = new ArrayList<>();
    }

    public List<Traslado> all(){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Traslado> criteriaQuery = criteriaBuilder.createQuery(Traslado.class);
        Root<Traslado> root = criteriaQuery.from(Traslado.class);
        criteriaQuery.select(root);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
    public Traslado save(Traslado traslado) {
        if (Objects.isNull(traslado.getId())) {
            //traslado.setId(seqId.getAndIncrement());
            //this.traslados.add(traslado);
            this.entityManager.persist(traslado);

        }
        return traslado;
    }

    public List<Traslado> findByColaborador (Long colaboradorId){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Traslado> criteriaQuery = criteriaBuilder.createQuery(Traslado.class);
        Root<Traslado> trasladoRoot = criteriaQuery.from(Traslado.class);

        Predicate predicadoColaborador = criteriaBuilder.equal(trasladoRoot.get("colaboradorId"), colaboradorId);
        criteriaQuery.where(predicadoColaborador);
        List<Traslado> trasladosColaborador = entityManager.createQuery(criteriaQuery).getResultList();
        if(trasladosColaborador.isEmpty()){
            throw new NoSuchElementException("El colaborador solicitado no tiene traslados asignados");
        }
        return trasladosColaborador;
    }

    public Traslado findById(Long id) {
        return this.entityManager.find(Traslado.class, id);
        /*
        Optional<Traslado> first = this.traslados.stream().filter(x -> x.getId().equals(id)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay un traslado de id: %s", id)
        ));
        */
    }

    public void modificarEstadoTraslado(Long trasladoId, EstadoTrasladoEnum nuevoEstado) throws NoSuchElementException {
        Traslado trasladoViejo = this.findById(trasladoId);
        trasladoViejo.setEstado(nuevoEstado);
        entityManager.merge(trasladoViejo);
    }
}
