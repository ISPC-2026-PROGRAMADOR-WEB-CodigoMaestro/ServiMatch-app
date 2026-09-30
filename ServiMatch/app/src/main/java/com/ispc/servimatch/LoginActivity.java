package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etPassword;
    private Button btnIngresar;
    private TextView tvRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvRegistro = findViewById(R.id.tvRegistro);

        btnIngresar.setOnClickListener(v -> {

            String usuario = etUsuario.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (usuario.isEmpty()) {

                etUsuario.setError("Ingresá tu usuario");
                etUsuario.requestFocus();

            } else if (password.isEmpty()) {

                etPassword.setError("Ingresá tu contraseña");
                etPassword.requestFocus();

            } else {

                Intent intent = new Intent(
                        LoginActivity.this,
                        DashboardActivity.class
                );

                intent.putExtra("usuario", usuario);

                startActivity(intent);
            }



        });

        tvRegistro.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegistroActivity.class
            );

            startActivity(intent);

        });
    }
}