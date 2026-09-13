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

            if (!usuario.isEmpty() && !password.isEmpty()) {

                Intent intent = new Intent(
                        LoginActivity.this,
                        MainActivity.class
                );

                intent.putExtra("usuario", usuario);

                startActivity(intent);

            } else {

                Toast.makeText(
                        LoginActivity.this,
                        "Completá todos los campos",
                        Toast.LENGTH_SHORT
                ).show();

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