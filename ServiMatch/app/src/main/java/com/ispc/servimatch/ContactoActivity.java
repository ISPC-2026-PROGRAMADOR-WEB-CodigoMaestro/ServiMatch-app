package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactoActivity extends AppCompatActivity {

    private LinearLayout navInicio;
    private LinearLayout navContacto;
    private LinearLayout navGuardados;
    private LinearLayout navPerfil;

    private TextView iconContacto;
    private TextView textoContacto;

    private Spinner spinnerAsunto;
    private EditText etMensaje;
    private Button btnEnviarMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        // Vincular elementos de navegacion
        navInicio = findViewById(R.id.navInicio);
        navContacto = findViewById(R.id.navContacto);
        navGuardados = findViewById(R.id.navGuardados);
        navPerfil = findViewById(R.id.navPerfil);

        iconContacto = findViewById(R.id.iconContacto);
        textoContacto = findViewById(R.id.textoContacto);

        // Vincular elementos del formulario
        spinnerAsunto = findViewById(R.id.spinnerAsunto);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);

        // Marcar Contacto como seleccionado
        iconContacto.setTextColor(
                getResources().getColor(R.color.color_primario)
        );

        textoContacto.setTextColor(
                getResources().getColor(R.color.color_primario)
        );

        textoContacto.setTypeface(null, Typeface.BOLD);

        // Recuperar los datos del usuario
        String usuario = getIntent().getStringExtra("usuario");
        int idUsuario = getIntent().getIntExtra("id_usuario", -1);
        int idRol = getIntent().getIntExtra("id_rol", -1);
        String rol = getIntent().getStringExtra("rol");

        // Opciones del Spinner
        String[] asuntos = {
                "Seleccione un asunto",
                "Consulta general",
                "Sugerencia",
                "Reportar un problema",
                "Otro"
        };

        // Cargamos los asuntos en el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ContactoActivity.this,
                R.layout.spinner_item,
                asuntos
        );

        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerAsunto.setAdapter(adapter);

        // Boton enviar mensaje
        btnEnviarMensaje.setOnClickListener(v -> {

            String asunto = "";
            String mensaje = etMensaje.getText().toString().trim();

            // Obtener asunto seleccionado
            if (spinnerAsunto.getSelectedItemPosition() != 0) {
                asunto = spinnerAsunto.getSelectedItem().toString();
            }

            // Validar asunto
            if (asunto.isEmpty()) {
                Toast.makeText(
                        ContactoActivity.this,
                        "Seleccione un asunto",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Validar mensaje
            if (mensaje.isEmpty()) {
                Toast.makeText(
                        ContactoActivity.this,
                        "Ingrese un mensaje",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Validar usuario
            if (idUsuario == -1) {
                Toast.makeText(
                        ContactoActivity.this,
                        "No se pudo identificar al usuario",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Preparar los datos del mensaje
            Map<String, String> datos = new HashMap<>();
            datos.put("asunto", asunto);
            datos.put("mensaje", mensaje);

            // Conectar con el backend
            ApiService apiService = RetrofitClient
                    .getClient(ContactoActivity.this)
                    .create(ApiService.class);

            // Enviar mensaje
            btnEnviarMensaje.setEnabled(false);

            apiService.enviarContacto(datos).enqueue(
                    new Callback<Map<String, Object>>() {

                        @Override
                        public void onResponse(
                                Call<Map<String, Object>> call,
                                Response<Map<String, Object>> response) {

                            btnEnviarMensaje.setEnabled(true);

                            if (response.isSuccessful()) {

                                Toast.makeText(
                                        ContactoActivity.this,
                                        "Mensaje enviado correctamente",
                                        Toast.LENGTH_SHORT
                                ).show();

                                spinnerAsunto.setSelection(0);
                                etMensaje.setText("");

                            } else {

                                Toast.makeText(
                                        ContactoActivity.this,
                                        "No se pudo enviar el mensaje",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<Map<String, Object>> call,
                                Throwable t) {

                            btnEnviarMensaje.setEnabled(true);

                            Toast.makeText(
                                    ContactoActivity.this,
                                    "Error de conexión con el servidor",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

        });

        // Navegacion - Inicio
        navInicio.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ContactoActivity.this,
                    DashboardActivity.class
            );

            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);

            startActivity(intent);
            finish();
        });

        // Navegacion - Contacto
        navContacto.setOnClickListener(v -> {

            Toast.makeText(
                    ContactoActivity.this,
                    "Ya estas en contacto",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // Navegacion - Guardados
        navGuardados.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ContactoActivity.this,
                    FavoritosActivity.class
            );

            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);

            startActivity(intent);
            finish();
        });

        // Navegacion - Perfil
        navPerfil.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ContactoActivity.this,
                    PerfilActivity.class
            );

            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);

            startActivity(intent);
            finish();
        });
    }
}
