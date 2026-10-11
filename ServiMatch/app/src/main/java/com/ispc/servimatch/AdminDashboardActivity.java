
package com.ispc.servimatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView txtBienvenidaAdmin;
    private TextView btnUsuariosAdmin;
    private TextView btnOficiosAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =========================================
        // CARGAR PANTALLA DEL ADMINISTRADOR
        // =========================================

        setContentView(R.layout.activity_admin_dashboard);

        // =========================================
        // VINCULAR ELEMENTOS DEL DASHBOARD
        // =========================================

        txtBienvenidaAdmin =
                findViewById(R.id.txtBienvenidaAdmin);

        btnUsuariosAdmin =
                findViewById(R.id.btnUsuariosAdmin);

        btnOficiosAdmin =
                findViewById(R.id.btnOficiosAdmin);

        // =========================================
        // RECIBIR DATOS DESDE LOGIN
        // =========================================

        String usuario =
                getIntent().getStringExtra("usuario");

        // =========================================
        // MOSTRAR BIENVENIDA
        // =========================================

        if (usuario != null && !usuario.isEmpty()) {

            txtBienvenidaAdmin.setText(
                    "¡Hola, " + usuario + "!"
            );

        } else {

            txtBienvenidaAdmin.setText(
                    "¡Hola, Admin!"
            );
        }

        // =========================================
        // OPCIÓN GESTIÓN DE USUARIOS
        // =========================================

        btnUsuariosAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AdminUsuariosActivity.class
            );

            startActivity(intent);
        });

        // La opción Oficios se implementará después.
    }
}
