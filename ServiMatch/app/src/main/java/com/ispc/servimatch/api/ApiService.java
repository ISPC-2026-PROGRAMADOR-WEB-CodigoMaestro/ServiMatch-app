
package com.ispc.servimatch.api;

import com.ispc.servimatch.model.Ubicacion;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Usuario;
import com.ispc.servimatch.model.LoginRequest;
import com.ispc.servimatch.model.LoginResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface ApiService {

    // OBTENER UBICACIONES
    @GET("api/ubicaciones/")
    Call<List<Ubicacion>> obtenerUbicaciones();

    // OBTENER OFICIOS
    @GET("api/oficios/")
    Call<List<Oficio>> obtenerOficios();


    // OBTENER USUARIOS (ADMINISTRADOR)
    @GET("api/usuarios/")
    Call<List<Usuario>> obtenerUsuarios(
            @Header("Authorization") String token
    );


    // OBTENER PROFESIONALES
    @GET("api/profesionales/")
    Call<List<Usuario>> obtenerProfesionales(
            @Header("Authorization") String token
    );

    // OBTENER USUARIO POR ID
    @GET("api/usuarios/{id}/")
    Call<Usuario> obtenerUsuario(
            @Path("id") int id,
            @Header("Authorization") String token
    );

    // REGISTRAR USUARIO
    @POST("api/usuarios/")
    Call<Usuario> registrarUsuario(
            @Body Usuario usuario
    );

    // ACTUALIZAR USUARIO (ADMINISTRADOR)
    @PATCH("api/usuarios/{id}/")
    Call<Usuario> actualizarUsuario(
            @Path("id") int id,
            @Header("Authorization") String token,
            @Body Map<String, Object> datos
    );

    // INICIAR SESIÓN
    @POST("api/login/")
    Call<LoginResponse> login(
            @Body LoginRequest loginRequest
    );
}
