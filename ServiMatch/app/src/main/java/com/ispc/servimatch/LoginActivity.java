
package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

        // VINCULAR ELEMENTOS DEL LOGIN
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvRegistro = findViewById(R.id.tvRegistro);

        // BOTÓN INGRESAR
        btnIngresar.setOnClickListener(v -> {

            String usuario = etUsuario.getText()
                    .toString().trim();

            String password = etPassword.getText()
                    .toString();

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

        // IR A REGISTRO
        tvRegistro.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegistroActivity.class
            );

            startActivity(intent);
        });
    }

    // INICIAR SESIÓN CON DJANGO
    private void iniciarSesion(
            String emailOUsuario,
            String contrasena) {

        btnIngresar.setEnabled(false);

        LoginRequest request =
                new LoginRequest(emailOUsuario, contrasena);

        ApiService apiService =
                RetrofitClient.getClient()
                        .create(ApiService.class);

        apiService.login(request).enqueue(
                new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponse> call,
                            Response<LoginResponse> response) {

                        btnIngresar.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            LoginResponse loginResponse =
                                    response.body();

                            // OBTENER TOKEN JWT
                            String token = loginResponse.getAccess();

                            if (token == null || token.isEmpty()) {

                                Toast.makeText(
                                        LoginActivity.this,
                                        "El servidor no devolvió un token de acceso",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            // IDENTIFICAR ROL
                            int idRol = loginResponse.getId_rol();

                            Intent intent;

                            if (idRol == 10) {

                                // ADMINISTRADOR
                                intent = new Intent(
                                        LoginActivity.this,
                                        AdminDashboardActivity.class
                                );

                            } else if (idRol == 11) {

                                // USUARIO ESTÁNDAR
                                intent = new Intent(
                                        LoginActivity.this,
                                        DashboardActivity.class
                                );

                            } else {

                                Toast.makeText(
                                        LoginActivity.this,
                                        "El usuario no tiene un rol válido: " + idRol,
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            // GUARDAR TOKEN JWT Y DATOS DE SESIÓN
                            SharedPreferences preferencias =
                                    getSharedPreferences(
                                            "ServiMatchSesion",
                                            MODE_PRIVATE
                                    );

                            preferencias.edit()
                                    .putString("access_token", token)
                                    .putInt("id_usuario", loginResponse.getId())
                                    .putInt("id_rol", idRol)
                                    .apply();

                            // ENVIAR DATOS DEL USUARIO
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
                                    idRol
                            );

                            intent.putExtra(
                                    "rol",
                                    loginResponse.getRol()
                            );

                            // LOGIN EXITOSO
                            Toast.makeText(
                                    LoginActivity.this,
                                    loginResponse.getMensaje(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            startActivity(intent);

                            finish();

                        } else {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "No se pudo iniciar sesión. Código: "
                                            + response.code(),
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
