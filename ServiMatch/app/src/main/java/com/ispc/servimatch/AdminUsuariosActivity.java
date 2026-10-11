
package com.ispc.servimatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

    // BARRA DE NAVEGACIÓN
    private LinearLayout navInicioAdmin;
    private LinearLayout navUsuariosAdmin;
    private LinearLayout navPerfilAdmin;
    private LinearLayout navSalirAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_usuarios);

        // VINCULAR CONTENEDOR
        contenedorUsuariosAdmin =
                findViewById(R.id.contenedorUsuariosAdmin);

        // VINCULAR BARRA DE NAVEGACIÓN
        navInicioAdmin =
                findViewById(R.id.navInicioAdmin);

        navUsuariosAdmin =
                findViewById(R.id.navUsuariosAdmin);

        navPerfilAdmin =
                findViewById(R.id.navPerfilAdmin);

        navSalirAdmin =
                findViewById(R.id.navSalirAdmin);

        // CONFIGURAR MENÚ
        configurarNavegacion();

        // CARGAR USUARIOS DESDE DJANGO
        cargarUsuarios();
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

        // USUARIOS: PANTALLA ACTUAL
        navUsuariosAdmin.setOnClickListener(v -> {
            // Ya estamos en el listado de usuarios.
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

        // LIMPIAR SESIÓN PRINCIPAL
        SharedPreferences sesion =
                getSharedPreferences(
                        "ServiMatchSesion",
                        MODE_PRIVATE
                );

        sesion.edit().clear().apply();

        // LIMPIAR TOKEN UTILIZADO POR RETROFIT
        SharedPreferences preferencias =
                getSharedPreferences(
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

            Toast.makeText(
                    this,
                    "No hay una sesión activa. Iniciá sesión nuevamente.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // CREAR SERVICIO RETROFIT CON CONTEXT
        ApiService apiService =
                RetrofitClient.getClient(AdminUsuariosActivity.this)
                        .create(ApiService.class);

        // CONSULTAR USUARIOS CON JWT
        apiService.obtenerUsuarios("Bearer " + token).enqueue(
                new Callback<List<Usuario>>() {

                    @Override
                    public void onResponse(
                            Call<List<Usuario>> call,
                            Response<List<Usuario>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Usuario> usuarios = response.body();

                            // ELIMINAR TARJETAS ANTERIORES
                            // CONSERVAR EL TÍTULO
                            while (contenedorUsuariosAdmin.getChildCount() > 1) {
                                contenedorUsuariosAdmin.removeViewAt(1);
                            }

                            if (usuarios.isEmpty()) {

                                Toast.makeText(
                                        AdminUsuariosActivity.this,
                                        "No hay usuarios registrados",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            // MOSTRAR USUARIOS
                            for (Usuario usuario : usuarios) {

                                View tarjeta = LayoutInflater.from(
                                        AdminUsuariosActivity.this
                                ).inflate(
                                        R.layout.item_usuario_admin,
                                        contenedorUsuariosAdmin,
                                        false
                                );

                                TextView txtNombre =
                                        tarjeta.findViewById(
                                                R.id.txtNombreUsuarioAdmin
                                        );

                                TextView txtEmail =
                                        tarjeta.findViewById(
                                                R.id.txtEmailUsuarioAdmin
                                        );

                                TextView txtRol =
                                        tarjeta.findViewById(
                                                R.id.txtRolUsuarioAdmin
                                        );

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

                                contenedorUsuariosAdmin.addView(tarjeta);
                            }

                        } else if (response.code() == 401) {

                            Toast.makeText(
                                    AdminUsuariosActivity.this,
                                    "La sesión expiró. Iniciá sesión nuevamente.",
                                    Toast.LENGTH_LONG
                            ).show();

                        } else if (response.code() == 403) {

                            Toast.makeText(
                                    AdminUsuariosActivity.this,
                                    "No tenés permisos de administrador.",
                                    Toast.LENGTH_LONG
                            ).show();

                        } else {

                            Toast.makeText(
                                    AdminUsuariosActivity.this,
                                    "Error al obtener usuarios. Código: "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Usuario>> call,
                            Throwable t) {

                        Toast.makeText(
                                AdminUsuariosActivity.this,
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
