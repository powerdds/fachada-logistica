package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.ClassFinder;

public interface TestTP<T> {

    String PAQUETE_BASE = "ar.edu.utn.dds.k3003.";
    String PAQUETE_IMPL = PAQUETE_BASE + "app.Fachada";

    String paquete();

    Class<T> clase();

    default boolean corresponde() {
        try {
            this.instance();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    default T instance() throws Exception {
        return ClassFinder.findAndInstantiateClassImplementingInterface(PAQUETE_IMPL, clase());
    }
}