package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.LoginRequest;
import com.ispc.servimatch.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

            if (usuario.isEmpty() && password.isEmpty()) {

                Toast.makeText(
                        LoginActivity.this,
                        "Completá el correo electrónico y la contraseña",
                        Toast.LENGTH_SHORT
                ).show();

            } else if (usuario.isEmpty()) {

                Toast.makeText(
                        LoginActivity.this,
                        "Ingresá tu correo electrónico",
                        Toast.LENGTH_SHORT
                ).show();

            } else if (password.isEmpty()) {

                Toast.makeText(
                        LoginActivity.this,
                        "Ingresá tu contraseña",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                iniciarSesion(usuario, password);
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

    private void iniciarSesion(String emailOUsuario, String contrasena) {

        btnIngresar.setEnabled(false);

        LoginRequest request =
                new LoginRequest(emailOUsuario, contrasena);

        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(
                    Call<LoginResponse> call,
                    Response<LoginResponse> response) {

                btnIngresar.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse loginResponse = response.body();

                    Toast.makeText(
                            LoginActivity.this,
                            loginResponse.getMensaje(),
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            LoginActivity.this,
                            DashboardActivity.class
                    );

                    // Datos del usuario que vienen desde Django
                    intent.putExtra(
                            "usuario",
                            loginResponse.getNombre()
                    );

                    intent.putExtra(
                            "id_usuario",
                            loginResponse.getId()
                    );

                    intent.putExtra(
                            "id_rol",
                            loginResponse.getId_rol()
                    );

                    intent.putExtra(
                            "rol",
                            loginResponse.getRol()
                    );

                    startActivity(intent);

                    // Evita volver al login con el botón atrás
                    finish();

                } else {

                    Toast.makeText(
                            LoginActivity.this,
                            "Correo electrónico o contraseña incorrectos",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<LoginResponse> call,
                    Throwable t) {

                btnIngresar.setEnabled(true);

                Toast.makeText(
                        LoginActivity.this,
                        "Error de conexión con el servidor",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}