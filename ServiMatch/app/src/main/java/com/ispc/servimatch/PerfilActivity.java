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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Ubicacion;
import com.ispc.servimatch.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = "SERVIMATCH_PERFIL";

    private TextView tvNombre, tvRol, tvEmail, tvDni, tvUbicacion, tvOficio;
    private Button btnEditarPerfil, btnEliminarPerfil, btnCerrarSesion;

    private View barraNavigation, barraNavigationAdmin;

    private LinearLayout navInicio, navContacto, navPerfil;
    private LinearLayout navInicioAdmin, navUsuariosAdmin, navPerfilAdmin, navSalirAdmin;

    private int idUsuario, idRol;
    private String nombreUsuario, rolUsuario;

    private SharedPreferences preferencias;
    private boolean eliminando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // RECUPERAR SESIÓN
        preferencias = getSharedPreferences("ServiMatchSesion", MODE_PRIVATE);

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
        tvOficio = findViewById(R.id.tvOficio);

        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnEliminarPerfil = findViewById(R.id.btnEliminarPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // MOSTRAR BOTÓN SOLO A USUARIOS ESTÁNDAR
        if (idRol == 11) {
            btnEliminarPerfil.setVisibility(View.VISIBLE);
        } else {
            btnEliminarPerfil.setVisibility(View.GONE);
        }

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

        // VALIDAR ID DEL USUARIO
        if (idUsuario == -1) {
            Log.e(TAG, "No se encontró el ID del usuario");
            Toast.makeText(this,
                    "No se pudo identificar al usuario. Iniciá sesión nuevamente.",
                    Toast.LENGTH_LONG).show();
        }

        // BOTÓN EDITAR PERFIL
        btnEditarPerfil.setOnClickListener(v -> {
            if (idUsuario == -1) {
                Toast.makeText(this,
                        "No se pudo identificar al usuario",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(PerfilActivity.this, EditarPerfilActivity.class);
            intent.putExtra("id_usuario", idUsuario);
            startActivity(intent);
        });

        // BOTÓN ELIMINAR CUENTA
        btnEliminarPerfil.setOnClickListener(v -> confirmarEliminacion());

        // BOTÓN CERRAR SESIÓN DEL PERFIL
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    // ACTUALIZAR DATOS DEL PERFIL
    @Override
    protected void onResume() {
        super.onResume();

        if (idUsuario > 0) {
            buscarUsuario();
        }
    }

    // CONFIGURAR NAVEGACIÓN SEGÚN ROL
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
            Toast.makeText(this,
                    "No se pudo identificar el rol del usuario.",
                    Toast.LENGTH_LONG).show();
        }
    }

    // NAVEGACIÓN USUARIO ESTÁNDAR
    private void configurarNavegacionEstandar() {

        // MARCAR PERFIL COMO SELECCIONADO
        TextView iconPerfil = findViewById(R.id.iconPerfil);
        TextView textoPerfil = findViewById(R.id.textoPerfil);

        iconPerfil.setTextColor(getColor(R.color.color_primario));
        textoPerfil.setTextColor(getColor(R.color.color_primario));
        textoPerfil.setTypeface(null, Typeface.BOLD);

        // INICIO
        navInicio.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, DashboardActivity.class);

            intent.putExtra("usuario", nombreUsuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rolUsuario);

            startActivity(intent);
            finish();
        });

        // CONTACTO
        navContacto.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, ContactoActivity.class);

            intent.putExtra("usuario", nombreUsuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rolUsuario);

            startActivity(intent);
            finish();
        });

        // PERFIL
        navPerfil.setOnClickListener(v -> {
            Toast.makeText(this, "Ya estás en Perfil", Toast.LENGTH_SHORT).show();
        });
    }

    // NAVEGACIÓN ADMINISTRADOR
    private void configurarNavegacionAdmin() {

        // INICIO ADMINISTRADOR
        navInicioAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        });

        // GESTIÓN DE USUARIOS
        navUsuariosAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, AdminUsuariosActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        });

        // PERFIL ADMINISTRADOR
        navPerfilAdmin.setOnClickListener(v -> {
            Toast.makeText(this, "Ya estás en Perfil", Toast.LENGTH_SHORT).show();
        });

        // SALIR
        navSalirAdmin.setOnClickListener(v -> cerrarSesion());
    }

    // CONFIRMAR ELIMINACIÓN
    private void confirmarEliminacion() {

        if (eliminando || idUsuario <= 0) {
            return;
        }

        if (idRol != 11) {
            mostrarError("No tenés permisos para eliminar esta cuenta.");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar mi cuenta")
                .setMessage("¿Estás seguro de que querés eliminar tu cuenta?"
                        + "\n\nEsta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarUsuario();
                })
                .show();
    }

    // ELIMINAR USUARIO DESDE DJANGO
    private void eliminarUsuario() {

        if (eliminando || idUsuario <= 0) {
            return;
        }

        SharedPreferences preferencias = getSharedPreferences(
                "ServiMatchSesion", MODE_PRIVATE
        );

        String token = preferencias.getString("access_token", null);
        int idSesion = preferencias.getInt("id_usuario", -1);
        int idRol = preferencias.getInt("id_rol", -1);

        // VALIDAR USUARIO ESTÁNDAR
        if (idRol != 11 || idSesion <= 0 || idUsuario != idSesion) {
            mostrarError("No tenés permisos para realizar esta eliminación.");
            return;
        }

        // VALIDAR TOKEN
        if (token == null || token.isEmpty()) {
            mostrarError("La sesión expiró. Iniciá sesión nuevamente.");
            return;
        }

        eliminando = true;
        btnEliminarPerfil.setEnabled(false);

        // INICIALIZAR RETROFIT
        ApiService apiService = RetrofitClient.getClient(PerfilActivity.this)
                .create(ApiService.class);

        // ELIMINAR USUARIO
        apiService.eliminarUsuario(idUsuario, "Bearer " + token)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (isFinishing() || isDestroyed()) return;

                        Log.d(TAG, "Eliminar usuario - HTTP: " + response.code());

                        if (response.code() == 204) {
                            Toast.makeText(PerfilActivity.this,
                                    "Tu cuenta fue eliminada correctamente.",
                                    Toast.LENGTH_LONG).show();

                            // CERRAR SESIÓN Y VOLVER AL LOGIN
                            cerrarSesion();

                        } else {
                            eliminando = false;
                            btnEliminarPerfil.setEnabled(true);

                            if (response.code() == 401) {
                                mostrarError("La sesión expiró. Iniciá sesión nuevamente.");
                            } else if (response.code() == 403) {
                                mostrarError("No tenés permisos para eliminar esta cuenta.");
                            } else if (response.code() == 404) {
                                mostrarError("La cuenta no existe.");
                            } else {
                                mostrarError("Error al eliminar la cuenta. Código: "
                                        + response.code());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (isFinishing() || isDestroyed()) return;

                        eliminando = false;
                        btnEliminarPerfil.setEnabled(true);

                        Log.e(TAG, "Error al eliminar usuario", t);
                        mostrarError("Error de conexión. No se pudo confirmar la eliminación.");
                    }
                });
    }

    // CERRAR SESIÓN
    private void cerrarSesion() {

        // ELIMINAR SESIÓN PRINCIPAL
        preferencias.edit().clear().apply();

        // ELIMINAR TOKEN DE RETROFIT
        getSharedPreferences("ServiMatchPrefs", MODE_PRIVATE)
                .edit().clear().apply();

        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    // OBTENEMOS EL TOKEN JWT
    private void buscarUsuario() {

        String token = preferencias.getString("access_token", null);

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "No se encontró el token JWT");

            Toast.makeText(this,
                    "La sesión no está activa. Iniciá sesión nuevamente.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "ID usuario: " + idUsuario + " | Token presente: true");

        // CONSULTAMOS EL USUARIO MEDIANTE APISERVICE Y RETROFIT
        ApiService apiService = RetrofitClient.getClient(PerfilActivity.this)
                .create(ApiService.class);

        apiService.obtenerUsuario(idUsuario, "Bearer " + token)
                .enqueue(new Callback<Usuario>() {

                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        Log.d(TAG, "Respuesta HTTP: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            Usuario usuario = response.body();
                            mostrarUsuario(usuario);

                        } else if (response.code() == 401) {
                            mostrarError("Error 401: La sesión expiró o el token no es válido.");
                        } else if (response.code() == 403) {
                            mostrarError("Error 403: No tenés permisos para consultar este perfil.");
                        } else if (response.code() == 404) {
                            mostrarError("Error 404: No se encontró el usuario.");
                        } else {
                            mostrarError("Error al obtener el perfil. Código HTTP: "
                                    + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Log.e(TAG, "Error de conexión", t);
                        mostrarError("Error de conexión con el servidor: " + t.getMessage());
                    }
                });
    }

    // MOSTRAR DATOS DEL USUARIO
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

        // CONSULTAR OFICIO
        if (usuario.getOficio() != null) {
            buscarOficio(usuario.getOficio());
        } else {
            tvOficio.setText("Sin oficio registrado");
        }

        Log.d(TAG, "Datos del usuario cargados correctamente");
    }

    // OBTENER UBICACIÓN
    private void buscarUbicacion(int idUbicacion) {

        ApiService apiService = RetrofitClient.getClient(PerfilActivity.this)
                .create(ApiService.class);

        apiService.obtenerUbicaciones().enqueue(new Callback<List<Ubicacion>>() {

            @Override
            public void onResponse(Call<List<Ubicacion>> call,
                                   Response<List<Ubicacion>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    boolean encontrada = false;

                    for (Ubicacion ubicacion : response.body()) {
                        if (ubicacion.getId() == idUbicacion) {
                            tvUbicacion.setText(ubicacion.getCiudad()
                                    + ", " + ubicacion.getProvincia());

                            encontrada = true;
                            break;
                        }
                    }

                    if (!encontrada) {
                        tvUbicacion.setText("Ubicación no disponible");
                    }

                } else {
                    Log.e(TAG, "Error al consultar ubicaciones: " + response.code());
                    tvUbicacion.setText("Ubicación no disponible");
                }
            }

            @Override
            public void onFailure(Call<List<Ubicacion>> call, Throwable t) {
                Log.e(TAG, "Error al consultar ubicación", t);
                tvUbicacion.setText("Ubicación no disponible");
            }
        });
    }

    // OBTENER OFICIO
    private void buscarOficio(int idOficio) {

        ApiService apiService = RetrofitClient.getClient(PerfilActivity.this)
                .create(ApiService.class);

        apiService.obtenerOficios().enqueue(new Callback<List<Oficio>>() {

            @Override
            public void onResponse(Call<List<Oficio>> call,
                                   Response<List<Oficio>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    boolean encontrado = false;

                    for (Oficio oficio : response.body()) {
                        if (oficio.getId() == idOficio) {
                            tvOficio.setText(oficio.getNombre_oficio());
                            encontrado = true;
                            break;
                        }
                    }

                    if (!encontrado) {
                        tvOficio.setText("Oficio no disponible");
                    }

                } else {
                    tvOficio.setText("Oficio no disponible");
                }
            }

            @Override
            public void onFailure(Call<List<Oficio>> call, Throwable t) {
                Log.e(TAG, "Error al consultar oficio", t);
                tvOficio.setText("Oficio no disponible");
            }
        });
    }

    // MOSTRAR ERRORES
    private void mostrarError(String mensaje) {
        Log.e(TAG, mensaje);
        Toast.makeText(PerfilActivity.this, mensaje, Toast.LENGTH_LONG).show();
    }
}