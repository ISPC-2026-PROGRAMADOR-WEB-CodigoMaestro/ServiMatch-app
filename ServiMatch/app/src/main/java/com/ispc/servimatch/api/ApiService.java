package com.ispc.servimatch.api;

import com.ispc.servimatch.model.Ubicacion;
import com.ispc.servimatch.model.Oficio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("api/ubicaciones/")
    Call<List<Ubicacion>> obtenerUbicaciones();

    @GET("api/oficios/")
    Call<List<Oficio>> obtenerOficios();

}