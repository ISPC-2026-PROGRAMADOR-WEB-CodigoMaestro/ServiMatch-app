
package com.ispc.servimatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsuariosActivity extends AppCompatActivity {

    // CONTENEDOR DEL LISTADO
    private LinearLayout contenedorUsuariosAdmin;

    // BOTÓN AGREGAR USUARIO
    private Button btnAgregarUsuarioAdmin;

    // BARRA DE NAVEGACIÓN
    private LinearLayout navInicioAdmin;
    private LinearLayout navUsuariosAdmin;
    private LinearLayout navPerfilAdmin;
    private LinearLayout navSalirAdmin;

    // SERVICIO API
    private ApiService apiService;

    // CONTROL DE CONSULTAS
    private Call<List<Usuario>> consultaUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_usuarios);

        // VINCULAR CONTENEDOR
        contenedorUsuariosAdmin =
                findViewById(R.id.contenedorUsuariosAdmin);

        // VINCULAR BOTÓN AGREGAR USUARIO
        btnAgregarUsuarioAdmin =
                findViewById(R.id.btnAgregarUsuarioAdmin);

        // VINCULAR BARRA DE NAVEGACIÓN
        navInicioAdmin = findViewById(R.id.navInicioAdmin);
        navUsuariosAdmin = findViewById(R.id.navUsuariosAdmin);
        navPerfilAdmin = findViewById(R.id.navPerfilAdmin);
        navSalirAdmin = findViewById(R.id.navSalirAdmin);

        // INICIALIZAR RETROFIT
        apiService = RetrofitClient
                .getClient(AdminUsuariosActivity.this)
                .create(ApiService.class);

        // CONFIGURAR NAVEGACIÓN
        configurarNavegacion();

        // CONFIGURAR BOTÓN AGREGAR USUARIO
        configurarBotonAgregarUsuario();
    }

    // =====================================
    // ACTUALIZAR LISTADO AL REGRESAR
    // =====================================
    @Override
    protected void onResume() {
        super.onResume();

        // CONSULTAR NUEVAMENTE LOS USUARIOS
        cargarUsuarios();
    }

    // =====================================
    // CONFIGURAR BOTÓN AGREGAR USUARIO
    // =====================================
    private void configurarBotonAgregarUsuario() {

        btnAgregarUsuarioAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminUsuariosActivity.this,
                    AdminRegistrarUsuarioActivity.class
            );

            startActivity(intent);
        });
    }

    // =====================================
    // CONFIGURAR BARRA DE NAVEGACIÓN
    // =====================================
    private void configurarNavegacion() {

        // INICIO: VOLVER AL DASHBOARD
        navInicioAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminUsuariosActivity.this,
                    AdminDashboardActivity.class
            );

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        });

        // USUARIOS: ACTUALIZAR LISTADO
        navUsuariosAdmin.setOnClickListener(v -> {
            cargarUsuarios();
        });

        // PERFIL
        navPerfilAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminUsuariosActivity.this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });

        // SALIR
        navSalirAdmin.setOnClickListener(v -> {
            cerrarSesion();
        });
    }

    // =====================================
    // CERRAR SESIÓN
    // =====================================
    private void cerrarSesion() {

        // CANCELAR CONSULTA PENDIENTE
        if (consultaUsuarios != null) {
            consultaUsuarios.cancel();
        }

        // LIMPIAR SESIÓN PRINCIPAL
        SharedPreferences sesion = getSharedPreferences(
                "ServiMatchSesion",
                MODE_PRIVATE
        );

        sesion.edit().clear().apply();

        // LIMPIAR TOKEN DE RETROFIT
        SharedPreferences preferencias = getSharedPreferences(
                "ServiMatchPrefs",
                MODE_PRIVATE
        );

        preferencias.edit().clear().apply();

        // REGRESAR AL LOGIN
        Intent intent = new Intent(
                AdminUsuariosActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    // =====================================
    // CONSULTAR USUARIOS DESDE DJANGO
    // =====================================
    private void cargarUsuarios() {

        // RECUPERAR TOKEN JWT
        SharedPreferences preferencias = getSharedPreferences(
                "ServiMatchSesion",
                MODE_PRIVATE
        );

        String token = preferencias.getString(
                "access_token",
                null
        );

        if (token == null || token.isEmpty()) {

            mostrarMensaje(
                    "No hay una sesión activa. Iniciá sesión nuevamente."
            );

            return;
        }

        // CANCELAR CONSULTA ANTERIOR
        if (consultaUsuarios != null) {
            consultaUsuarios.cancel();
        }

        // NUEVA CONSULTA
        consultaUsuarios = apiService.obtenerUsuarios(
                "Bearer " + token
        );

        consultaUsuarios.enqueue(
                new Callback<List<Usuario>>() {

                    @Override
                    public void onResponse(
                            Call<List<Usuario>> call,
                            Response<List<Usuario>> response) {

                        if (call.isCanceled() || isFinishing()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Usuario> usuarios = response.body();

                            mostrarUsuarios(usuarios);

                        } else if (response.code() == 401) {

                            mostrarMensaje(
                                    "La sesión expiró. Iniciá sesión nuevamente."
                            );

                        } else if (response.code() == 403) {

                            mostrarMensaje(
                                    "No tenés permisos de administrador."
                            );

                        } else {

                            mostrarMensaje(
                                    "Error al obtener usuarios. Código: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Usuario>> call,
                            Throwable t) {

                        if (call.isCanceled() || isFinishing()) {
                            return;
                        }

                        mostrarMensaje(
                                "Error de conexión: " + t.getMessage()
                        );
                    }
                }
        );
    }

    // =====================================
    // MOSTRAR USUARIOS EN EL LISTADO
    // =====================================
    private void mostrarUsuarios(List<Usuario> usuarios) {

        // ELIMINAR TARJETAS ANTERIORES
        // CONSERVAR EL TÍTULO Y EL BOTÓN AGREGAR
        while (contenedorUsuariosAdmin.getChildCount() > 2) {

            contenedorUsuariosAdmin.removeViewAt(2);
        }

        // VERIFICAR SI HAY USUARIOS
        if (usuarios.isEmpty()) {

            mostrarMensaje("No hay usuarios registrados.");

            return;
        }

        // RECORRER USUARIOS
        for (Usuario usuario : usuarios) {

            // CREAR TARJETA
            View tarjeta = LayoutInflater.from(
                    AdminUsuariosActivity.this
            ).inflate(
                    R.layout.item_usuario_admin,
                    contenedorUsuariosAdmin,
                    false
            );

            // VINCULAR ELEMENTOS
            TextView txtNombre = tarjeta.findViewById(
                    R.id.txtNombreUsuarioAdmin
            );

            TextView txtEmail = tarjeta.findViewById(
                    R.id.txtEmailUsuarioAdmin
            );

            TextView txtRol = tarjeta.findViewById(
                    R.id.txtRolUsuarioAdmin
            );

            // MOSTRAR DATOS
            txtNombre.setText(usuario.getNombre());
            txtEmail.setText(usuario.getEmail());

            Integer rol = usuario.getRol();

            if (rol != null && rol == 10) {

                txtRol.setText("Administrador");

            } else if (rol != null && rol == 11) {

                txtRol.setText("Usuario estándar");

            } else {

                txtRol.setText("Rol no identificado");
            }

            // ABRIR DETALLE AL TOCAR LA TARJETA
            tarjeta.setOnClickListener(v -> {

                Intent intent = new Intent(
                        AdminUsuariosActivity.this,
                        AdminUsuarioDetalleActivity.class
                );

                // ENVIAR ID DEL USUARIO
                intent.putExtra(
                        "id_usuario",
                        usuario.getId()
                );

                startActivity(intent);
            });

            // AGREGAR TARJETA AL CONTENEDOR
            contenedorUsuariosAdmin.addView(tarjeta);
        }
    }

    // =====================================
    // MOSTRAR MENSAJES
    // =====================================
    private void mostrarMensaje(String mensaje) {

        Toast.makeText(
                AdminUsuariosActivity.this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }

    // =====================================
    // CANCELAR CONSULTAS AL CERRAR
    // =====================================
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (consultaUsuarios != null) {
            consultaUsuarios.cancel();
        }
    }
}
