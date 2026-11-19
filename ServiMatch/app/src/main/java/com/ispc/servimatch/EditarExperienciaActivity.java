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

public class EditarExperienciaActivity extends AppCompatActivity {

    private static final String TAG = "EDITAR_EXPERIENCIA";

    private EditText etPuestoEditar;
    private EditText etEmpresaEditar;
    private EditText etAnioInicioEditar;
    private EditText etAnioFinEditar;
    private EditText etDescripcionEditar;

    private Button btnGuardarCambiosExperiencia;
    private Button btnCancelarEditarExperiencia;

    private ApiService apiService;
    private String token;
    private int idExperiencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_experiencia);

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
        etPuestoEditar = findViewById(R.id.etPuestoEditar);
        etEmpresaEditar = findViewById(R.id.etEmpresaEditar);
        etAnioInicioEditar = findViewById(R.id.etAnioInicioEditar);
        etAnioFinEditar = findViewById(R.id.etAnioFinEditar);
        etDescripcionEditar = findViewById(R.id.etDescripcionEditar);

        btnGuardarCambiosExperiencia = findViewById(R.id.btnGuardarCambiosExperiencia);
        btnCancelarEditarExperiencia = findViewById(R.id.btnCancelarEditarExperiencia);

        // RECUPERAR DATOS DE LA EXPERIENCIA
        idExperiencia = getIntent().getIntExtra("id_experiencia", -1);

        String puesto = getIntent().getStringExtra("puesto");
        String empresa = getIntent().getStringExtra("empresa");
        int anioInicio = getIntent().getIntExtra("anio_inicio", 0);
        int anioFin = getIntent().getIntExtra("anio_fin", 0);
        String descripcion = getIntent().getStringExtra("descripcion");

        // VALIDAR EXPERIENCIA
        if (idExperiencia == -1) {
            Toast.makeText(this, "No se encontró la experiencia laboral", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // CARGAR DATOS EN EL FORMULARIO
        etPuestoEditar.setText(puesto);
        etEmpresaEditar.setText(empresa);
        etAnioInicioEditar.setText(String.valueOf(anioInicio));

        if (anioFin != 0) {
            etAnioFinEditar.setText(String.valueOf(anioFin));
        }

        etDescripcionEditar.setText(descripcion);

        // BOTÓN GUARDAR CAMBIOS
        btnGuardarCambiosExperiencia.setOnClickListener(v -> {
            actualizarExperiencia();
        });

        // BOTÓN CANCELAR
        btnCancelarEditarExperiencia.setOnClickListener(v -> {
            finish();
        });
    }

    // ACTUALIZAR EXPERIENCIA LABORAL
    private void actualizarExperiencia() {

        // OBTENER DATOS DEL FORMULARIO
        String puesto = etPuestoEditar.getText().toString().trim();
        String empresa = etEmpresaEditar.getText().toString().trim();
        String inicio = etAnioInicioEditar.getText().toString().trim();
        String fin = etAnioFinEditar.getText().toString().trim();
        String descripcion = etDescripcionEditar.getText().toString().trim();

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
        btnGuardarCambiosExperiencia.setEnabled(false);

        apiService.actualizarExperiencia(
                idExperiencia,
                "Bearer " + token,
                datos
        ).enqueue(new Callback<ExperienciaLaboral>() {

            @Override
            public void onResponse(
                    Call<ExperienciaLaboral> call,
                    Response<ExperienciaLaboral> response) {

                if (isFinishing() || isDestroyed()) return;

                btnGuardarCambiosExperiencia.setEnabled(true);

                Log.d(TAG, "Actualizar experiencia - HTTP: " + response.code());

                if (response.isSuccessful()) {

                    Toast.makeText(
                            EditarExperienciaActivity.this,
                            "Experiencia laboral actualizada correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else if (response.code() == 401) {

                    Toast.makeText(
                            EditarExperienciaActivity.this,
                            "Tu sesión expiró. Iniciá sesión nuevamente.",
                            Toast.LENGTH_LONG
                    ).show();

                } else if (response.code() == 403) {

                    Toast.makeText(
                            EditarExperienciaActivity.this,
                            "No tenés permisos para editar esta experiencia",
                            Toast.LENGTH_LONG
                    ).show();

                } else {

                    Toast.makeText(
                            EditarExperienciaActivity.this,
                            "Error al actualizar la experiencia. Código: "
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

                btnGuardarCambiosExperiencia.setEnabled(true);

                Log.e(TAG, "Error al actualizar experiencia", t);

                Toast.makeText(
                        EditarExperienciaActivity.this,
                        "Error de conexión con el servidor",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
