package ar.edu.utn.dds.k3003.controladores;

import ar.edu.utn.dds.k3003.app.Fachada;
import io.javalin.http.Context;

import javax.persistence.EntityManager;

public class DBController {

    private final Fachada fachada;
    private EntityManager entityManager;
    public DBController(Fachada fachada) {
        this.fachada = fachada;
        this.entityManager = fachada.getEntityManagerFactory().createEntityManager();
    }

    public void eliminarDB(Context context){
            entityManager.getTransaction().begin();
            try{
                entityManager.createQuery("DELETE FROM Traslado").executeUpdate();
                entityManager.createQuery("DELETE FROM Ruta").executeUpdate();
                entityManager.getTransaction().commit();
                context.result("Se borraron los datos correctamente.");
            }catch (RuntimeException e){
                entityManager.getTransaction().rollback();
                context.result("Error al borrar datos.");
                context.status(500);
                throw e;
            } finally {
                entityManager.close();
            }
    }
}
