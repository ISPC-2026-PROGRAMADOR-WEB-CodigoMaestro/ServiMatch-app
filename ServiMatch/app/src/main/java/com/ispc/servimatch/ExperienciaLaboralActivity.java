package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.ExperienciaLaboral;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExperienciaLaboralActivity extends AppCompatActivity {

    private static final String TAG = "SERVIMATCH_EXPERIENCIA";

    private LinearLayout contenedorExperiencias;
    private TextView tvSinExperiencias;
    private Button btnAgregarExperiencia;

    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiencia_laboral);

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
        contenedorExperiencias = findViewById(R.id.contenedorExperiencias);
        tvSinExperiencias = findViewById(R.id.tvSinExperiencias);
        btnAgregarExperiencia = findViewById(R.id.btnAgregarExperiencia);

        // BOTÓN AGREGAR EXPERIENCIA
        btnAgregarExperiencia.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ExperienciaLaboralActivity.this,
                    RegistrarExperienciaActivity.class
            );

            startActivity(intent);
            finish();
        });
    }

    // ACTUALIZAR EXPERIENCIAS
    @Override
    protected void onResume() {
        super.onResume();
        obtenerExperiencias();
    }

    // CONSULTAR EXPERIENCIAS CON JWT
    private void obtenerExperiencias() {

        if (token == null || token.isEmpty()) {

            tvSinExperiencias.setText(
                    "Iniciá sesión para consultar tus experiencias laborales."
            );

            tvSinExperiencias.setVisibility(View.VISIBLE);

            Toast.makeText(
                    this,
                    "No hay una sesión activa",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        apiService.obtenerExperiencias(
                "Bearer " + token
        ).enqueue(new Callback<List<ExperienciaLaboral>>() {

            @Override
            public void onResponse(
                    Call<List<ExperienciaLaboral>> call,
                    Response<List<ExperienciaLaboral>> response) {

                if (isFinishing() || isDestroyed()) return;

                Log.d(TAG, "Obtener experiencias - HTTP: " + response.code());

                if (response.isSuccessful()
                        && response.body() != null) {

                    mostrarExperiencias(response.body());

                } else if (response.code() == 401) {

                    tvSinExperiencias.setText(
                            "Tu sesión expiró. Iniciá sesión nuevamente."
                    );

                    tvSinExperiencias.setVisibility(View.VISIBLE);

                } else if (response.code() == 403) {

                    tvSinExperiencias.setText(
                            "No tenés permisos para consultar experiencias."
                    );

                    tvSinExperiencias.setVisibility(View.VISIBLE);

                } else {

                    Toast.makeText(
                            ExperienciaLaboralActivity.this,
                            "Error al obtener las experiencias. Código: "
                                    + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<List<ExperienciaLaboral>> call,
                    Throwable t) {

                if (isFinishing() || isDestroyed()) return;

                Log.e(TAG, "Error al obtener experiencias", t);

                Toast.makeText(
                        ExperienciaLaboralActivity.this,
                        "Error de conexión con el servidor",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    // MOSTRAR EXPERIENCIAS LABORALES
    private void mostrarExperiencias(List<ExperienciaLaboral> experiencias) {

        contenedorExperiencias.removeAllViews();

        if (experiencias.isEmpty()) {

            tvSinExperiencias.setText(
                    "Todavía no registraste experiencias laborales."
            );

            tvSinExperiencias.setVisibility(View.VISIBLE);
            return;
        }

        tvSinExperiencias.setVisibility(View.GONE);

        for (ExperienciaLaboral experiencia : experiencias) {

            // CREAR TARJETA
            View card = getLayoutInflater().inflate(
                    R.layout.item_experiencia_laboral,
                    contenedorExperiencias,
                    false
            );

            // VINCULAR COMPONENTES
            TextView tvPuestoExperiencia =
                    card.findViewById(R.id.tvPuestoExperiencia);

            TextView tvEmpresaExperiencia =
                    card.findViewById(R.id.tvEmpresaExperiencia);

            TextView tvPeriodoExperiencia =
                    card.findViewById(R.id.tvPeriodoExperiencia);

            TextView tvDescripcionExperiencia =
                    card.findViewById(R.id.tvDescripcionExperiencia);

            Button btnEditarExperiencia =
                    card.findViewById(R.id.btnEditarExperiencia);

            Button btnEliminarExperiencia =
                    card.findViewById(R.id.btnEliminarExperiencia);

            // MOSTRAR DATOS
            tvPuestoExperiencia.setText(
                    experiencia.getPuesto()
            );

            tvEmpresaExperiencia.setText(
                    experiencia.getEmpresa()
            );

            // MOSTRAR PERÍODO LABORAL
            String periodo = experiencia.getAnio_inicio() + " - ";

            if (experiencia.getAnio_fin() != null) {

                periodo += experiencia.getAnio_fin();

            } else {

                periodo += "Actualidad";
            }

            tvPeriodoExperiencia.setText(periodo);

            // MOSTRAR DESCRIPCIÓN
            if (experiencia.getDescripcion() != null
                    && !experiencia.getDescripcion().isEmpty()) {

                tvDescripcionExperiencia.setText(
                        experiencia.getDescripcion()
                );

            } else {

                tvDescripcionExperiencia.setVisibility(View.GONE);
            }

            // BOTÓN EDITAR EXPERIENCIA
            btnEditarExperiencia.setOnClickListener(v -> {

                Toast.makeText(
                        ExperienciaLaboralActivity.this,
                        "Próximamente podrás editar esta experiencia",
                        Toast.LENGTH_SHORT
                ).show();
            });

            // BOTÓN ELIMINAR EXPERIENCIA
            btnEliminarExperiencia.setOnClickListener(v -> {

                Toast.makeText(
                        ExperienciaLaboralActivity.this,
                        "Próximamente podrás eliminar esta experiencia",
                        Toast.LENGTH_SHORT
                ).show();
            });

            // AGREGAR TARJETA AL CONTENEDOR
            contenedorExperiencias.addView(card);
        }
    }
}
