package ar.edu.utn.dds.k3003.clientes;

import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import retrofit2.Call;
import retrofit2.http.*;

public interface ColaboradoresRetrofitClient {

    @POST("colaboradores/{id}/asignarTraslado")
    Call<Void> reportarTraslado(@Path("id") Long id, @Body TrasladoDTO trasladoDTO);

}