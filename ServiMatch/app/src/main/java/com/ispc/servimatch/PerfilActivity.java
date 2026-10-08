package com.ispc.servimatch;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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

    // Datos del perfil
    private TextView tvNombre;
    private TextView tvRol;
    private TextView tvEmail;
    private TextView tvDni;
    private TextView tvUbicacion;

    private Button btnCerrarSesion;

    // Barra de navegación
    private LinearLayout navInicio;
    private LinearLayout navContacto;
    private LinearLayout navPerfil;

    private TextView iconPerfil;
    private TextView textoPerfil;

    // Datos del usuario autenticado
    private int idUsuario;
    private int idRol;
    private String nombreUsuario;
    private String rolUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // =========================================
        // RECIBIR DATOS DEL USUARIO
        // =========================================

        idUsuario =
                getIntent().getIntExtra(
                        "id_usuario",
                        -1
                );

        idRol =
                getIntent().getIntExtra(
                        "id_rol",
                        -1
                );

        nombreUsuario =
                getIntent().getStringExtra(
                        "usuario"
                );

        rolUsuario =
                getIntent().getStringExtra(
                        "rol"
                );

        // =========================================
        // VINCULAR DATOS DEL PERFIL
        // =========================================

        tvNombre =
                findViewById(R.id.tvNombre);

        tvRol =
                findViewById(R.id.tvRol);

        tvEmail =
                findViewById(R.id.tvEmail);

        tvDni =
                findViewById(R.id.tvDni);

        tvUbicacion =
                findViewById(R.id.tvUbicacion);

        btnCerrarSesion =
                findViewById(R.id.btnCerrarSesion);

        // =========================================
        // VINCULAR BARRA DE NAVEGACIÓN
        // =========================================

        navInicio =
                findViewById(R.id.navInicio);

        navContacto =
                findViewById(R.id.navContacto);

        navPerfil =
                findViewById(R.id.navPerfil);

        iconPerfil =
                findViewById(R.id.iconPerfil);

        textoPerfil =
                findViewById(R.id.textoPerfil);

        // =========================================
        // MARCAR PERFIL COMO SELECCIONADO
        // =========================================

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

        // =========================================
        // VERIFICAR USUARIO
        // =========================================

        if (idUsuario != -1) {

            buscarUsuario();

        } else {

            Toast.makeText(
                    PerfilActivity.this,
                    "No se pudo identificar al usuario",
                    Toast.LENGTH_LONG
            ).show();
        }

        // =========================================
        // NAVEGACIÓN - INICIO
        // =========================================

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

        // =========================================
        // NAVEGACIÓN - CONTACTO
        // =========================================

        navContacto.setOnClickListener(v -> {

            Toast.makeText(
                    PerfilActivity.this,
                    "Próximamente: Contacto",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // =========================================
        // NAVEGACIÓN - PERFIL
        // =========================================

        navPerfil.setOnClickListener(v -> {

            Toast.makeText(
                    PerfilActivity.this,
                    "Ya estás en Perfil",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // =========================================
        // CERRAR SESIÓN
        // =========================================

        btnCerrarSesion.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        });
    }

    // =============================================
    // OBTENER USUARIO DEL BACKEND
    // =============================================

    private void buscarUsuario() {

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        apiService.obtenerUsuario(idUsuario).enqueue(
                new Callback<Usuario>() {

                    @Override
                    public void onResponse(
                            Call<Usuario> call,
                            Response<Usuario> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Usuario usuario =
                                    response.body();

                            mostrarUsuario(usuario);

                        } else {

                            Toast.makeText(
                                    PerfilActivity.this,
                                    "Error al obtener los datos del usuario",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Usuario> call,
                            Throwable t) {

                        Toast.makeText(
                                PerfilActivity.this,
                                "Error de conexión con el servidor",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =============================================
    // MOSTRAR DATOS DEL USUARIO
    // =============================================

    private void mostrarUsuario(
            Usuario usuario) {

        tvNombre.setText(
                usuario.getNombre()
        );

        tvEmail.setText(
                usuario.getEmail()
        );

        tvDni.setText(
                usuario.getDni()
        );

        // Rol recibido durante el Login
        if (rolUsuario != null
                && !rolUsuario.isEmpty()) {

            tvRol.setText(
                    rolUsuario
            );

        } else {

            tvRol.setText(
                    "Usuario"
            );
        }

        // Actualizamos el nombre para conservarlo
        // cuando volvemos al Dashboard.
        nombreUsuario =
                usuario.getNombre();

        // Buscar la ubicación
        if (usuario.getUbicacion() != null) {

            buscarUbicacion(
                    usuario.getUbicacion()
            );

        } else {

            tvUbicacion.setText(
                    "Ubicación no disponible"
            );
        }
    }

    // =============================================
    // OBTENER UBICACIÓN DEL USUARIO
    // =============================================

    private void buscarUbicacion(
            int idUbicacion) {

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        apiService.obtenerUbicaciones().enqueue(
                new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Ubicacion> ubicaciones =
                                    response.body();

                            boolean encontrada = false;

                            for (Ubicacion ubicacion :
                                    ubicaciones) {

                                if (ubicacion.getId()
                                        == idUbicacion) {

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

                            tvUbicacion.setText(
                                    "Ubicación no disponible"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        tvUbicacion.setText(
                                "Ubicación no disponible"
                        );
                    }
                });
    }
}