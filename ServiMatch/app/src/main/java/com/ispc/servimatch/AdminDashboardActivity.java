
package com.ispc.servimatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    // ELEMENTOS DEL DASHBOARD
    private TextView txtBienvenidaAdmin;
    private TextView btnUsuariosAdmin;
    private TextView btnOficiosAdmin;

    // ELEMENTOS DE LA BARRA DE NAVEGACIÓN
    private LinearLayout navInicioAdmin;
    private LinearLayout navUsuariosAdmin;
    private LinearLayout navPerfilAdmin;
    private LinearLayout navSalirAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // CARGAR PANTALLA DEL ADMINISTRADOR
        setContentView(R.layout.activity_admin_dashboard);

        // VINCULAR ELEMENTOS DEL DASHBOARD
        txtBienvenidaAdmin =
                findViewById(R.id.txtBienvenidaAdmin);

        btnUsuariosAdmin =
                findViewById(R.id.btnUsuariosAdmin);

        btnOficiosAdmin =
                findViewById(R.id.btnOficiosAdmin);

        // VINCULAR ELEMENTOS DE NAVEGACIÓN
        navInicioAdmin =
                findViewById(R.id.navInicioAdmin);

        navUsuariosAdmin =
                findViewById(R.id.navUsuariosAdmin);

        navPerfilAdmin =
                findViewById(R.id.navPerfilAdmin);

        navSalirAdmin =
                findViewById(R.id.navSalirAdmin);

        // RECIBIR DATOS DESDE LOGIN
        String usuario =
                getIntent().getStringExtra("usuario");

        // MOSTRAR BIENVENIDA
        if (usuario != null && !usuario.isEmpty()) {

            txtBienvenidaAdmin.setText(
                    "¡Hola, " + usuario + "!"
            );

        } else {

            txtBienvenidaAdmin.setText(
                    "¡Hola, Admin!"
            );
        }

        // =====================================
        // TARJETA: GESTIÓN DE USUARIOS
        // =====================================
        btnUsuariosAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminUsuariosActivity.class
            );

            startActivity(intent);
        });

        // =====================================
        // TARJETA: GESTIÓN DE OFICIOS
        // =====================================
        btnOficiosAdmin.setOnClickListener(v -> {

            Toast.makeText(
                    AdminDashboardActivity.this,
                    "Gestión de oficios próximamente",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // =====================================
        // MENÚ: INICIO
        // =====================================
        navInicioAdmin.setOnClickListener(v -> {

            // Ya estamos en el dashboard.
        });

        // =====================================
        // MENÚ: USUARIOS
        // =====================================
        navUsuariosAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminUsuariosActivity.class
            );

            startActivity(intent);
        });

        // =====================================
        // MENÚ: PERFIL
        // =====================================
        navPerfilAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });

        // =====================================
        // MENÚ: SALIR
        // =====================================
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

        // LIMPIAR TOKEN DE RETROFIT
        SharedPreferences preferencias =
                getSharedPreferences(
                        "ServiMatchPrefs",
                        MODE_PRIVATE
                );

        preferencias.edit().clear().apply();

        // REGRESAR AL LOGIN
        Intent intent = new Intent(
                AdminDashboardActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
