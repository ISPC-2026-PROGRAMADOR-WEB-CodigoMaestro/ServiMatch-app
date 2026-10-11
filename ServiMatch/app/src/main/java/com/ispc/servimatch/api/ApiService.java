
package com.ispc.servimatch.api;

import com.ispc.servimatch.model.Ubicacion;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Usuario;
import com.ispc.servimatch.model.LoginRequest;
import com.ispc.servimatch.model.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface ApiService {

    // Obtener ubicaciones
    @GET("api/ubicaciones/")
    Call<List<Ubicacion>> obtenerUbicaciones();

    // Obtener oficios
    @GET("api/oficios/")
    Call<List<Oficio>> obtenerOficios();

    // Obtener usuarios con autenticación JWT
    @GET("api/usuarios/")
    Call<List<Usuario>> obtenerUsuarios(
            @Header("Authorization") String token
    );

    // Obtener un usuario por ID con autenticación JWT
    @GET("api/usuarios/{id}/")
    Call<Usuario> obtenerUsuario(
            @Path("id") int id,
            @Header("Authorization") String token
    );

    // Registrar usuario
    @POST("api/usuarios/")
    Call<Usuario> registrarUsuario(
            @Body Usuario usuario
    );

    // Login
    @POST("api/login/")
    Call<LoginResponse> login(
            @Body LoginRequest loginRequest
    );
}
