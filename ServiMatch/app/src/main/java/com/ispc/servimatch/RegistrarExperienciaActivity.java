package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.ExperienciaLaboral;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarExperienciaActivity extends AppCompatActivity {

    private static final String TAG = "SERVIMATCH_EXPERIENCIA";

    private EditText etPuestoExperiencia;
    private EditText etEmpresaExperiencia;
    private EditText etAnioInicio;
    private EditText etAnioFin;
    private EditText etDescripcionExperiencia;

    private Button btnGuardarExperiencia;
    private Button btnCancelarExperiencia;

    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_experiencia);

        // INICIALIZAR RETROFIT
        apiService = RetrofitClient.getClient(this)
                .create(ApiService.class);

        // RECUPERAR SESIÓN
        SharedPreferences preferencias = getSharedPreferences(
                "ServiMatchSesion",
                MODE_PRIVATE
        );

        token = preferencias.getString("access_token", null);

        // VINCULAR COMPONENTES
        etPuestoExperiencia = findViewById(R.id.etPuestoExperiencia);
        etEmpresaExperiencia = findViewById(R.id.etEmpresaExperiencia);
        etAnioInicio = findViewById(R.id.etAnioInicio);
        etAnioFin = findViewById(R.id.etAnioFin);
        etDescripcionExperiencia = findViewById(R.id.etDescripcionExperiencia);

        btnGuardarExperiencia = findViewById(R.id.btnGuardarExperiencia);
        btnCancelarExperiencia = findViewById(R.id.btnCancelarExperiencia);

        // BOTÓN GUARDAR EXPERIENCIA
        btnGuardarExperiencia.setOnClickListener(v -> {
            registrarExperiencia();
        });

        // BOTÓN CANCELAR
        btnCancelarExperiencia.setOnClickListener(v -> {
            finish();
        });
    }

    // REGISTRAR EXPERIENCIA LABORAL
    private void registrarExperiencia() {

        // OBTENER DATOS DEL FORMULARIO
        String puesto = etPuestoExperiencia.getText().toString().trim();
        String empresa = etEmpresaExperiencia.getText().toString().trim();
        String inicio = etAnioInicio.getText().toString().trim();
        String fin = etAnioFin.getText().toString().trim();
        String descripcion = etDescripcionExperiencia.getText().toString().trim();

        // VALIDAR CAMPOS OBLIGATORIOS
        if (puesto.isEmpty() || empresa.isEmpty()
                || inicio.isEmpty() || descripcion.isEmpty()) {

            Toast.makeText(
                    this,
                    "Completá todos los campos obligatorios",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // VALIDAR PUESTO DE TRABAJO
        if (!puesto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+")) {
            Toast.makeText(this,
                    "El puesto de trabajo solo puede contener letras",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // VALIDAR FECHAS
        if (inicio.length() != 4 || (!fin.isEmpty() && fin.length() != 4)) {
            Toast.makeText(this, "Ingresá años válidos de 4 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        int anioInicio;
        Integer anioFin = null;

        try {
            anioInicio = Integer.parseInt(inicio);

            if (!fin.isEmpty()) {
                anioFin = Integer.parseInt(fin);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresá años válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (anioFin != null && anioFin < anioInicio) {
            Toast.makeText(this, "El año de finalización no puede ser anterior al de inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        // VALIDAR SESIÓN
        if (token == null || token.isEmpty()) {

            Toast.makeText(
                    this,
                    "No hay una sesión activa",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // PREPARAR DATOS
        Map<String, Object> datos = new HashMap<>();

        datos.put("puesto", puesto);
        datos.put("empresa", empresa);
        datos.put("anio_inicio", anioInicio);
        datos.put("anio_fin", anioFin);
        datos.put("descripcion", descripcion);

        // ENVIAR DATOS AL BACKEND
        btnGuardarExperiencia.setEnabled(false);

        apiService.registrarExperiencia(
                "Bearer " + token,
                datos
        ).enqueue(new Callback<ExperienciaLaboral>() {

            @Override
            public void onResponse(
                    Call<ExperienciaLaboral> call,
                    Response<ExperienciaLaboral> response) {

                if (isFinishing() || isDestroyed()) return;

                btnGuardarExperiencia.setEnabled(true);

                Log.d(TAG, "Registrar experiencia - HTTP: " + response.code());

                if (response.isSuccessful()) {

                    Toast.makeText(
                            RegistrarExperienciaActivity.this,
                            "Experiencia laboral registrada correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else if (response.code() == 401) {

                    Toast.makeText(
                            RegistrarExperienciaActivity.this,
                            "Tu sesión expiró. Iniciá sesión nuevamente.",
                            Toast.LENGTH_LONG
                    ).show();

                } else if (response.code() == 403) {

                    Toast.makeText(
                            RegistrarExperienciaActivity.this,
                            "No tenés permisos para registrar experiencias",
                            Toast.LENGTH_LONG
                    ).show();

                } else {

                    Toast.makeText(
                            RegistrarExperienciaActivity.this,
                            "Error al registrar la experiencia. Código: "
                                    + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<ExperienciaLaboral> call,
                    Throwable t) {

                if (isFinishing() || isDestroyed()) return;

                btnGuardarExperiencia.setEnabled(true);

                Log.e(TAG, "Error al registrar experiencia", t);

                Toast.makeText(
                        RegistrarExperienciaActivity.this,
                        "Error de conexión con el servidor",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
