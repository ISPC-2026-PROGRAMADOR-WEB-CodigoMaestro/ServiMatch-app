package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtBienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("LOGIN_DEBUG", "ENTRÉ A DASHBOARD");

        setContentView(R.layout.activity_dashboard);

        txtBienvenida = findViewById(R.id.txtBienvenida);

        String usuario = getIntent().getStringExtra("usuario");

        if (usuario != null && !usuario.isEmpty()) {
            txtBienvenida.setText("¡Hola, " + usuario + "!");
        }

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnCerrarSesion.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        });
    }
}