
package com.ispc.servimatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Ubicacion;
import com.ispc.servimatch.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = "SERVIMATCH_PERFIL";

    // DATOS DEL PERFIL
    private TextView tvNombre;
    private TextView tvRol;
    private TextView tvEmail;
    private TextView tvDni;
    private TextView tvUbicacion;

    private Button btnCerrarSesion;

    // BARRA DE NAVEGACIÓN
    private LinearLayout navInicio;
    private LinearLayout navContacto;
    private LinearLayout navPerfil;

    private TextView iconPerfil;
    private TextView textoPerfil;

    // DATOS DEL USUARIO
    private int idUsuario;
    private int idRol;
    private String nombreUsuario;
    private String rolUsuario;

    private SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil);

        // RECUPERAR SESIÓN
        preferencias = getSharedPreferences(
                "ServiMatchSesion",
                MODE_PRIVATE
        );

        // OBTENER ID DEL USUARIO AUTENTICADO
        idUsuario = preferencias.getInt(
                "id_usuario",
                -1
        );

        idRol = preferencias.getInt(
                "id_rol",
                -1
        );

        // DATOS COMPLEMENTARIOS DEL INTENT
        nombreUsuario = getIntent().getStringExtra(
                "usuario"
        );

        rolUsuario = getIntent().getStringExtra(
                "rol"
        );

        // VINCULAR DATOS DEL PERFIL
        tvNombre = findViewById(R.id.tvNombre);
        tvRol = findViewById(R.id.tvRol);
        tvEmail = findViewById(R.id.tvEmail);
        tvDni = findViewById(R.id.tvDni);
        tvUbicacion = findViewById(R.id.tvUbicacion);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // VINCULAR BARRA DE NAVEGACIÓN
        navInicio = findViewById(R.id.navInicio);
        navContacto = findViewById(R.id.navContacto);
        navPerfil = findViewById(R.id.navPerfil);

        iconPerfil = findViewById(R.id.iconPerfil);
        textoPerfil = findViewById(R.id.textoPerfil);

        // MARCAR PERFIL COMO SELECCIONADO
        iconPerfil.setTextColor(
                getResources().getColor(
                        R.color.color_primario
                )
        );

        textoPerfil.setTextColor(
                getResources().getColor(
                        R.color.color_primario
                )
        );

        textoPerfil.setTypeface(
                null,
                Typeface.BOLD
        );

        // VERIFICAR SESIÓN
        if (idUsuario != -1) {

            buscarUsuario();

        } else {

            Log.e(TAG, "No se encontró el ID del usuario en la sesión");

            Toast.makeText(
                    this,
                    "No se pudo identificar al usuario. Iniciá sesión nuevamente.",
                    Toast.LENGTH_LONG
            ).show();
        }

        // NAVEGACIÓN - INICIO
        navInicio.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    DashboardActivity.class
            );

            intent.putExtra(
                    "usuario",
                    nombreUsuario
            );

            intent.putExtra(
                    "id_usuario",
                    idUsuario
            );

            intent.putExtra(
                    "id_rol",
                    idRol
            );

            intent.putExtra(
                    "rol",
                    rolUsuario
            );

            startActivity(intent);
            finish();
        });

        // NAVEGACIÓN - CONTACTO
        navContacto.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Próximamente: Contacto",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // NAVEGACIÓN - PERFIL
        navPerfil.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Ya estás en Perfil",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // CERRAR SESIÓN
        btnCerrarSesion.setOnClickListener(v -> {

            // ELIMINAR DATOS DE SESIÓN
            preferencias.edit().clear().apply();

            Intent intent = new Intent(
                    PerfilActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    // OBTENER USUARIO DESDE DJANGO
    private void buscarUsuario() {

        // RECUPERAR TOKEN JWT
        String token = preferencias.getString(
                "access_token",
                null
        );

        // VERIFICAR TOKEN
        if (token == null || token.isEmpty()) {

            Log.e(TAG, "No se encontró el token JWT");

            Toast.makeText(
                    this,
                    "La sesión no está activa. Iniciá sesión nuevamente.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // INFORMACIÓN PARA DIAGNÓSTICO
        Log.d(
                TAG,
                "ID usuario: " + idUsuario
                        + " | Token presente: true"
        );

        // CREAR SERVICIO RETROFIT
        ApiService apiService =
                RetrofitClient.getClient()
                        .create(ApiService.class);

        // CONSULTAR USUARIO CON JWT
        apiService.obtenerUsuario(
                idUsuario,
                "Bearer " + token
        ).enqueue(new Callback<Usuario>() {

            @Override
            public void onResponse(
                    Call<Usuario> call,
                    Response<Usuario> response) {

                Log.d(
                        TAG,
                        "Respuesta HTTP: " + response.code()
                );

                if (response.isSuccessful()
                        && response.body() != null) {

                    Usuario usuario = response.body();

                    mostrarUsuario(usuario);

                } else if (response.code() == 401) {

                    mostrarError(
                            "Error 401: La sesión expiró o el token no es válido."
                    );

                } else if (response.code() == 403) {

                    mostrarError(
                            "Error 403: No tenés permisos para consultar este perfil."
                    );

                } else if (response.code() == 404) {

                    mostrarError(
                            "Error 404: No se encontró el usuario."
                    );

                } else {

                    mostrarError(
                            "Error al obtener el perfil. Código HTTP: "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<Usuario> call,
                    Throwable t) {

                Log.e(
                        TAG,
                        "Error de conexión",
                        t
                );

                mostrarError(
                        "Error de conexión con el servidor: "
                                + t.getMessage()
                );
            }
        });
    }

    // MOSTRAR DATOS DEL USUARIO
    private void mostrarUsuario(Usuario usuario) {

        tvNombre.setText(
                usuario.getNombre()
        );

        tvEmail.setText(
                usuario.getEmail()
        );

        tvDni.setText(
                usuario.getDni()
        );

        // ACTUALIZAR NOMBRE
        nombreUsuario = usuario.getNombre();

        // MOSTRAR ROL
        Integer rol = usuario.getRol();

        if (rol != null && rol == 10) {

            rolUsuario = "Administrador";

        } else if (rol != null && rol == 11) {

            rolUsuario = "Usuario estándar";

        } else if (rolUsuario == null || rolUsuario.isEmpty()) {

            rolUsuario = "Usuario";
        }

        tvRol.setText(rolUsuario);

        // CONSULTAR UBICACIÓN
        if (usuario.getUbicacion() != null) {

            buscarUbicacion(
                    usuario.getUbicacion()
            );

        } else {

            tvUbicacion.setText(
                    "Ubicación no disponible"
            );
        }

        Log.d(
                TAG,
                "Datos del usuario cargados correctamente"
        );
    }

    // OBTENER UBICACIÓN
    private void buscarUbicacion(int idUbicacion) {

        ApiService apiService =
                RetrofitClient.getClient()
                        .create(ApiService.class);

        apiService.obtenerUbicaciones().enqueue(
                new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            boolean encontrada = false;

                            for (Ubicacion ubicacion : response.body()) {

                                if (ubicacion.getId() == idUbicacion) {

                                    tvUbicacion.setText(
                                            ubicacion.getCiudad()
                                                    + ", "
                                                    + ubicacion.getProvincia()
                                    );

                                    encontrada = true;
                                    break;
                                }
                            }

                            if (!encontrada) {

                                tvUbicacion.setText(
                                        "Ubicación no disponible"
                                );
                            }

                        } else {

                            Log.e(
                                    TAG,
                                    "Error al consultar ubicaciones: "
                                            + response.code()
                            );

                            tvUbicacion.setText(
                                    "Ubicación no disponible"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        Log.e(
                                TAG,
                                "Error al consultar ubicación",
                                t
                        );

                        tvUbicacion.setText(
                                "Ubicación no disponible"
                        );
                    }
                });
    }

    // MOSTRAR Y REGISTRAR ERRORES
    private void mostrarError(String mensaje) {

        Log.e(TAG, mensaje);

        Toast.makeText(
                PerfilActivity.this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }
}
