
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

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvRegistro = findViewById(R.id.tvRegistro);

        btnIngresar.setOnClickListener(v -> {

            String usuario = etUsuario.getText()
                    .toString().trim();

            String password = etPassword.getText()
                    .toString();

            if (usuario.isEmpty() && password.isEmpty()) {

                Toast.makeText(
                        this,
                        "Completá el correo electrónico y la contraseña",
                        Toast.LENGTH_SHORT
                ).show();

            } else if (usuario.isEmpty()) {

                Toast.makeText(
                        this,
                        "Ingresá tu correo electrónico",
                        Toast.LENGTH_SHORT
                ).show();

            } else if (password.isEmpty()) {

                Toast.makeText(
                        this,
                        "Ingresá tu contraseña",
                        Toast.LENGTH_SHORT
                ).show();

            } else if (LoginValidator.validarCampos(usuario, password)) {
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
                RetrofitClient.getClient(LoginActivity.this)
                        .create(ApiService.class);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(
                    Call<LoginResponse> call,
                    Response<LoginResponse> response) {

                btnIngresar.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse loginResponse = response.body();

                    String token = loginResponse.getAccess();

                    if (token == null || token.isEmpty()) {

                        Toast.makeText(
                                LoginActivity.this,
                                "El servidor no devolvió un token de acceso",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

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

                    // Guardar JWT y datos de sesión
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

                    // Compatibilidad con los cambios de develop
                    SharedPreferences preferences =
                            getSharedPreferences(
                                    "ServiMatchPrefs",
                                    MODE_PRIVATE
                            );

                    preferences.edit()
                            .putString("jwt_token", token)
                            .apply();

                    // Datos enviados al dashboard
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

                    Toast.makeText(
                            LoginActivity.this,
                            loginResponse.getMensaje(),
                            Toast.LENGTH_SHORT
                    ).show();

                    startActivity(intent);
                    finish();

                } else {

                    int codigoError = response.code();
                    String mensajeError;

                    switch (codigoError) {

                        case 401:
                            mensajeError =
                                    "Correo electrónico o contraseña incorrectos";
                            break;

                        case 429:
                            mensajeError =
                                    "Demasiados intentos. Esperá un minuto e intentá nuevamente";
                            break;

                        case 403:
                            mensajeError =
                                    "No tenés permisos para iniciar sesión";
                            break;

                        case 500:
                            mensajeError =
                                    "Error interno del servidor";
                            break;

                        default:
                            mensajeError =
                                    "Error al iniciar sesión. Código HTTP: "
                                            + codigoError;
                            break;
                    }

                    Toast.makeText(
                            LoginActivity.this,
                            mensajeError,
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
