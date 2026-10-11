
package com.ispc.servimatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Usuario;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Ubicacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsuarioDetalleActivity extends AppCompatActivity {

    // DATOS DEL USUARIO
    private TextView txtDetalleNombre;
    private TextView txtDetalleEmail;
    private TextView txtDetalleDni;
    private TextView txtDetalleRol;
    private TextView txtDetalleOficio;
    private TextView txtDetalleUbicacion;

    // BOTONES
    private Button btnEditarUsuario;
    private Button btnVolverUsuarios;

    // API
    private ApiService apiService;

    // ID DEL USUARIO SELECCIONADO
    private int idUsuario = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_usuario_detalle);

        // VINCULAR CAMPOS
        txtDetalleNombre = findViewById(R.id.txtDetalleNombre);
        txtDetalleEmail = findViewById(R.id.txtDetalleEmail);
        txtDetalleDni = findViewById(R.id.txtDetalleDni);
        txtDetalleRol = findViewById(R.id.txtDetalleRol);
        txtDetalleOficio = findViewById(R.id.txtDetalleOficio);
        txtDetalleUbicacion = findViewById(R.id.txtDetalleUbicacion);

        // VINCULAR BOTONES
        btnEditarUsuario = findViewById(R.id.btnEditarUsuario);
        btnVolverUsuarios = findViewById(R.id.btnVolverUsuarios);

        // INICIALIZAR RETROFIT
        apiService = RetrofitClient
                .getClient(AdminUsuarioDetalleActivity.this)
                .create(ApiService.class);

        // RECUPERAR ID DEL USUARIO
        idUsuario = getIntent().getIntExtra(
                "id_usuario",
                -1
        );

        if (idUsuario == -1) {

            mostrarError("No se recibió el ID del usuario.");

            btnEditarUsuario.setEnabled(false);
            return;
        }

        // VOLVER AL LISTADO DE USUARIOS
        btnVolverUsuarios.setOnClickListener(v -> {
            finish();
        });

        // ABRIR PANTALLA DE EDICIÓN
        btnEditarUsuario.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminUsuarioDetalleActivity.this,
                    AdminEditarUsuarioActivity.class
            );

            // ENVIAR ID DEL USUARIO A MODIFICAR
            intent.putExtra("id_usuario", idUsuario);

            startActivity(intent);
        });
    }

    // ==========================================
    // ACTUALIZAR DATOS AL VOLVER A LA PANTALLA
    // ==========================================
    @Override
    protected void onResume() {
        super.onResume();

        if (apiService != null && idUsuario > 0) {
            cargarDetalleUsuario(idUsuario);
        }
    }

    // ==========================================
    // OBTENER DATOS DEL USUARIO DESDE DJANGO
    // ==========================================
    private void cargarDetalleUsuario(int idUsuario) {

        SharedPreferences preferencias =
                getSharedPreferences(
                        "ServiMatchSesion",
                        MODE_PRIVATE
                );

        String token = preferencias.getString(
                "access_token",
                null
        );

        if (token == null || token.isEmpty()) {

            mostrarError("Iniciá sesión nuevamente.");
            return;
        }

        apiService.obtenerUsuario(
                idUsuario,
                "Bearer " + token
        ).enqueue(new Callback<Usuario>() {

            @Override
            public void onResponse(
                    Call<Usuario> call,
                    Response<Usuario> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    Usuario usuario = response.body();

                    // MOSTRAR DATOS PERSONALES
                    txtDetalleNombre.setText(usuario.getNombre());
                    txtDetalleEmail.setText(usuario.getEmail());
                    txtDetalleDni.setText(usuario.getDni());

                    // MOSTRAR ROL
                    Integer rol = usuario.getRol();

                    if (rol != null && rol == 10) {

                        txtDetalleRol.setText("Administrador");

                    } else if (rol != null && rol == 11) {

                        txtDetalleRol.setText("Usuario estándar");

                    } else {

                        txtDetalleRol.setText("Rol no identificado");
                    }

                    // CONSULTAR OFICIO Y UBICACIÓN
                    cargarOficio(usuario.getOficio());
                    cargarUbicacion(usuario.getUbicacion());

                } else if (response.code() == 401) {

                    mostrarError(
                            "La sesión expiró. Iniciá sesión nuevamente."
                    );

                } else if (response.code() == 403) {

                    mostrarError(
                            "No tenés permisos para consultar este usuario."
                    );

                } else if (response.code() == 404) {

                    mostrarError("El usuario no existe.");

                } else {

                    mostrarError(
                            "Error al obtener el usuario. Código: "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<Usuario> call,
                    Throwable t) {

                mostrarError(
                        "Error de conexión con el servidor."
                );
            }
        });
    }

    // ==========================================
    // OBTENER NOMBRE DEL OFICIO
    // ==========================================
    private void cargarOficio(Integer idOficio) {

        if (idOficio == null) {

            txtDetalleOficio.setText("No especificado");
            return;
        }

        apiService.obtenerOficios().enqueue(
                new Callback<List<Oficio>>() {

                    @Override
                    public void onResponse(
                            Call<List<Oficio>> call,
                            Response<List<Oficio>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            for (Oficio oficio : response.body()) {

                                if (oficio.getId() == idOficio) {

                                    txtDetalleOficio.setText(
                                            oficio.getNombre_oficio()
                                    );
                                    return;
                                }
                            }

                            txtDetalleOficio.setText("No encontrado");

                        } else {

                            txtDetalleOficio.setText("No disponible");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Oficio>> call,
                            Throwable t) {

                        txtDetalleOficio.setText("No disponible");
                    }
                });
    }

    // ==========================================
    // OBTENER CIUDAD Y PROVINCIA
    // ==========================================
    private void cargarUbicacion(Integer idUbicacion) {

        if (idUbicacion == null) {

            txtDetalleUbicacion.setText("No especificada");
            return;
        }

        apiService.obtenerUbicaciones().enqueue(
                new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            for (Ubicacion ubicacion : response.body()) {

                                if (ubicacion.getId() == idUbicacion) {

                                    txtDetalleUbicacion.setText(
                                            ubicacion.getCiudad()
                                                    + ", "
                                                    + ubicacion.getProvincia()
                                    );
                                    return;
                                }
                            }

                            txtDetalleUbicacion.setText("No encontrada");

                        } else {

                            txtDetalleUbicacion.setText("No disponible");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        txtDetalleUbicacion.setText("No disponible");
                    }
                });
    }

    // ==========================================
    // MOSTRAR MENSAJES DE ERROR
    // ==========================================
    private void mostrarError(String mensaje) {

        Toast.makeText(
                AdminUsuarioDetalleActivity.this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }
}
