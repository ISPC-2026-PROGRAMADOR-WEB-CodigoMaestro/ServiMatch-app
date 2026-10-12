
package com.ispc.servimatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    // ELEMENTOS DEL DASHBOARD
    private TextView txtBienvenidaAdmin;
    private TextView btnUsuariosAdmin;
    private TextView btnOficiosAdmin;
    private TextView btnUbicacionesAdmin;

    // ELEMENTOS DE LA BARRA DE NAVEGACIÓN
    private LinearLayout navInicioAdmin;
    private LinearLayout navUsuariosAdmin;
    private LinearLayout navPerfilAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // CARGAR DE PANTALLA DEL ADMINISTRADOR
        setContentView(R.layout.activity_admin_dashboard);

        // VINCULAR ELEMENTOS DEL DASHBOARD
        txtBienvenidaAdmin =
                findViewById(R.id.txtBienvenidaAdmin);

        btnUsuariosAdmin =
                findViewById(R.id.btnUsuariosAdmin);

        btnOficiosAdmin =
                findViewById(R.id.btnOficiosAdmin);

        btnUbicacionesAdmin =
                findViewById(R.id.btnUbicacionesAdmin);

        // VINCULAR ELEMENTOS DE NAVEGACIÓN
        navInicioAdmin =
                findViewById(R.id.navInicioAdmin);

        navUsuariosAdmin =
                findViewById(R.id.navUsuariosAdmin);

        navPerfilAdmin =
                findViewById(R.id.navPerfilAdmin);

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

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminOficiosActivity.class
            );

            startActivity(intent);
        });

        // =====================================
        // TARJETA: GESTIÓN DE UBICACIONES
        // =====================================
        btnUbicacionesAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminUbicacionesActivity.class
            );

            startActivity(intent);
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
    }
}
