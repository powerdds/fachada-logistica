package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.complementos.Ruta;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
@Getter
@Setter

public class RutaRepository {
    private EntityManager entityManager;
    private static AtomicLong seqId = new AtomicLong();
    //private Collection<Ruta> rutas;

    public RutaRepository() {

        //this.rutas = new ArrayList<>();
    }


    public RutaRepository(EntityManager entityManager){
        super();
        this.entityManager = entityManager;
    }

    public List<Ruta> all(){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ruta> criteriaQuery = criteriaBuilder.createQuery(Ruta.class);
        Root<Ruta> root = criteriaQuery.from(Ruta.class);
        criteriaQuery.select(root);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public Ruta save(Ruta ruta) {
        if (Objects.isNull(ruta.getId())) {
            //ruta.setId(seqId.getAndIncrement());
            //this.rutas.add(ruta);
            this.entityManager.persist(ruta);
        }
        return ruta;
    }

    public Ruta findById(Long id) {

        return this.entityManager.find(Ruta.class, id);
        /*
        Optional<Ruta> first = this.rutas.stream().filter(x -> x.getId().equals(id)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay una ruta de id: %s", id)
        ));
        */
    }

    public List<Ruta> findByHeladeras(Integer heladeraIdOrigen, Integer heladeraIdDestino) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ruta> criteriaQuery = criteriaBuilder.createQuery(Ruta.class);
        Root<Ruta> root = criteriaQuery.from(Ruta.class);
        Predicate predicadoParaHeladeraOrigen = criteriaBuilder.equal(root.get("heladeraIdOrigen"), heladeraIdOrigen);
        Predicate predicadoParaHeladeraDestino = criteriaBuilder.equal(root.get("heladeraIdDestino"), heladeraIdDestino);

        Predicate predicadoFinal = criteriaBuilder.and(predicadoParaHeladeraOrigen, predicadoParaHeladeraDestino);

        criteriaQuery.where(predicadoFinal);

        return entityManager.createQuery(criteriaQuery).getResultList();
        /*
        return this.rutas.stream().filter(x -> x.getHeladeraIdOrigen().equals(heladeraOrigen) &&
                x.getHeladeraIdDestino().equals(heladeraDestino)
        ).toList();
         */
    }
}
