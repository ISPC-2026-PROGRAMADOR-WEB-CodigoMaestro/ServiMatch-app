
package com.ispc.servimatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    // CONTENEDORES DE NAVEGACIÓN
    private View barraNavigation;
    private View barraNavigationAdmin;

    // NAVEGACIÓN ESTÁNDAR
    private LinearLayout navInicio;
    private LinearLayout navContacto;
    private LinearLayout navPerfil;

    // NAVEGACIÓN ADMINISTRADOR
    private LinearLayout navInicioAdmin;
    private LinearLayout navUsuariosAdmin;
    private LinearLayout navPerfilAdmin;
    private LinearLayout navSalirAdmin;

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

        idUsuario = preferencias.getInt("id_usuario", -1);
        idRol = preferencias.getInt("id_rol", -1);

        nombreUsuario = getIntent().getStringExtra("usuario");
        rolUsuario = getIntent().getStringExtra("rol");

        // VINCULAR DATOS DEL PERFIL
        tvNombre = findViewById(R.id.tvNombre);
        tvRol = findViewById(R.id.tvRol);
        tvEmail = findViewById(R.id.tvEmail);
        tvDni = findViewById(R.id.tvDni);
        tvUbicacion = findViewById(R.id.tvUbicacion);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // VINCULAR CONTENEDORES DE NAVEGACIÓN
        barraNavigation = findViewById(R.id.barraNavigation);
        barraNavigationAdmin = findViewById(R.id.barraNavigationAdmin);

        // VINCULAR MENÚ ESTÁNDAR
        navInicio = findViewById(R.id.navInicio);
        navContacto = findViewById(R.id.navContacto);
        navPerfil = findViewById(R.id.navPerfil);

        // VINCULAR MENÚ ADMINISTRADOR
        navInicioAdmin = findViewById(R.id.navInicioAdmin);
        navUsuariosAdmin = findViewById(R.id.navUsuariosAdmin);
        navPerfilAdmin = findViewById(R.id.navPerfilAdmin);
        navSalirAdmin = findViewById(R.id.navSalirAdmin);

        // MOSTRAR BARRA SEGÚN EL ROL
        configurarNavegacion();

        // CONSULTAR DATOS DEL PERFIL
        if (idUsuario != -1) {
            buscarUsuario();
        } else {
            Log.e(TAG, "No se encontró el ID del usuario");

            Toast.makeText(
                    this,
                    "No se pudo identificar al usuario. Iniciá sesión nuevamente.",
                    Toast.LENGTH_LONG
            ).show();
        }

        // BOTÓN CERRAR SESIÓN DEL PERFIL
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    // ==========================================
    // CONFIGURAR NAVEGACIÓN SEGÚN ROL
    // ==========================================
    private void configurarNavegacion() {

        // OCULTAR AMBAS BARRAS INICIALMENTE
        barraNavigation.setVisibility(View.GONE);
        barraNavigationAdmin.setVisibility(View.GONE);

        if (idRol == 10) {

            // ADMINISTRADOR
            barraNavigationAdmin.setVisibility(View.VISIBLE);
            configurarNavegacionAdmin();

        } else if (idRol == 11) {

            // USUARIO ESTÁNDAR
            barraNavigation.setVisibility(View.VISIBLE);
            configurarNavegacionEstandar();

        } else {

            Log.e(TAG, "Rol no identificado: " + idRol);

            Toast.makeText(
                    this,
                    "No se pudo identificar el rol del usuario.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // ==========================================
    // NAVEGACIÓN USUARIO ESTÁNDAR
    // ==========================================
    private void configurarNavegacionEstandar() {

        // MARCAR PERFIL COMO SELECCIONADO
        TextView iconPerfil = findViewById(R.id.iconPerfil);
        TextView textoPerfil = findViewById(R.id.textoPerfil);

        iconPerfil.setTextColor(
                getColor(R.color.color_primario)
        );

        textoPerfil.setTextColor(
                getColor(R.color.color_primario)
        );

        textoPerfil.setTypeface(null, Typeface.BOLD);

        // INICIO
        navInicio.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    DashboardActivity.class
            );

            intent.putExtra("usuario", nombreUsuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rolUsuario);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        });

        // CONTACTO
        navContacto.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Próximamente: Contacto",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // PERFIL
        navPerfil.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Ya estás en Perfil",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // ==========================================
    // NAVEGACIÓN ADMINISTRADOR
    // ==========================================
    private void configurarNavegacionAdmin() {

        // INICIO ADMINISTRADOR
        navInicioAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    AdminDashboardActivity.class
            );

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        });

        // GESTIÓN DE USUARIOS
        navUsuariosAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    AdminUsuariosActivity.class
            );

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        });

        // PERFIL ADMINISTRADOR
        navPerfilAdmin.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Ya estás en Perfil",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // SALIR
        navSalirAdmin.setOnClickListener(v -> cerrarSesion());
    }

    // ==========================================
    // CERRAR SESIÓN
    // ==========================================
    private void cerrarSesion() {

        // ELIMINAR SESIÓN PRINCIPAL
        preferencias.edit().clear().apply();

        // ELIMINAR TOKEN DE RETROFIT
        getSharedPreferences(
                "ServiMatchPrefs",
                MODE_PRIVATE
        ).edit().clear().apply();

        // REGRESAR AL LOGIN
        Intent intent = new Intent(
                PerfilActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    // ==========================================
    // OBTENER USUARIO DESDE DJANGO
    // ==========================================
    private void buscarUsuario() {

        String token = preferencias.getString(
                "access_token",
                null
        );

        if (token == null || token.isEmpty()) {

            Log.e(TAG, "No se encontró el token JWT");

            Toast.makeText(
                    this,
                    "La sesión no está activa. Iniciá sesión nuevamente.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Log.d(
                TAG,
                "ID usuario: " + idUsuario
                        + " | Token presente: true"
        );

        // INICIALIZAR RETROFIT
        ApiService apiService =
                RetrofitClient.getClient(PerfilActivity.this)
                        .create(ApiService.class);

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

                Log.e(TAG, "Error de conexión", t);

                mostrarError(
                        "Error de conexión con el servidor: "
                                + t.getMessage()
                );
            }
        });
    }

    // ==========================================
    // MOSTRAR DATOS DEL USUARIO
    // ==========================================
    private void mostrarUsuario(Usuario usuario) {

        tvNombre.setText(usuario.getNombre());
        tvEmail.setText(usuario.getEmail());
        tvDni.setText(usuario.getDni());

        nombreUsuario = usuario.getNombre();

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

            buscarUbicacion(usuario.getUbicacion());

        } else {

            tvUbicacion.setText("Ubicación no disponible");
        }

        Log.d(
                TAG,
                "Datos del usuario cargados correctamente"
        );
    }

    // ==========================================
    // OBTENER UBICACIÓN
    // ==========================================
    private void buscarUbicacion(int idUbicacion) {

        ApiService apiService =
                RetrofitClient.getClient(PerfilActivity.this)
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

    // ==========================================
    // MOSTRAR ERRORES
    // ==========================================
    private void mostrarError(String mensaje) {

        Log.e(TAG, mensaje);

        Toast.makeText(
                PerfilActivity.this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }
}
