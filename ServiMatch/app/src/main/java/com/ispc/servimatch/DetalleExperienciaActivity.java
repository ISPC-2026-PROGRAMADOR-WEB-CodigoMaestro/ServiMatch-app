package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.ExperienciaLaboral;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleExperienciaActivity extends AppCompatActivity {

    private static final String TAG = "DETALLE_EXPERIENCIA";

    private TextView tvNombreDetalleProfesional;
    private TextView tvSinExperienciasProfesional;
    private LinearLayout contenedorExperienciasProfesional;

    private ApiService apiService;
    private String token;
    private int idProfesional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_experiencia);

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
        tvNombreDetalleProfesional = findViewById(R.id.tvNombreDetalleProfesional);
        tvSinExperienciasProfesional = findViewById(R.id.tvSinExperienciasProfesional);
        contenedorExperienciasProfesional = findViewById(R.id.contenedorExperienciasProfesional);

        // RECUPERAR DATOS DEL PROFESIONAL
        idProfesional = getIntent().getIntExtra("id_profesional", -1);

        String nombreProfesional = getIntent().getStringExtra("nombre_profesional");

        if (nombreProfesional != null) {
            tvNombreDetalleProfesional.setText(nombreProfesional);
        }

        // VALIDAR PROFESIONAL
        if (idProfesional == -1) {
            Toast.makeText(
                    this,
                    "No se encontró el profesional",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
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

        // OBTENER EXPERIENCIAS LABORALES
        obtenerExperienciasProfesional();
    }

    // OBTENER EXPERIENCIAS DEL PROFESIONAL
    private void obtenerExperienciasProfesional() {

        apiService.obtenerExperienciasProfesional(
                idProfesional,
                "Bearer " + token
        ).enqueue(new Callback<List<ExperienciaLaboral>>() {

            @Override
            public void onResponse(
                    Call<List<ExperienciaLaboral>> call,
                    Response<List<ExperienciaLaboral>> response) {

                if (isFinishing() || isDestroyed()) return;

                Log.d(TAG, "Obtener experiencias - HTTP: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    mostrarExperiencias(response.body());

                } else {

                    Toast.makeText(
                            DetalleExperienciaActivity.this,
                            "Error al obtener las experiencias laborales",
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
                        DetalleExperienciaActivity.this,
                        "Error de conexión con el servidor",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    // MOSTRAR EXPERIENCIAS LABORALES
    private void mostrarExperiencias(List<ExperienciaLaboral> experiencias) {

        contenedorExperienciasProfesional.removeAllViews();

        // VERIFICAR SI HAY EXPERIENCIAS
        if (experiencias.isEmpty()) {

            tvSinExperienciasProfesional.setVisibility(View.VISIBLE);
            return;
        }

        tvSinExperienciasProfesional.setVisibility(View.GONE);

        // RECORRER EXPERIENCIAS
        for (ExperienciaLaboral experiencia : experiencias) {

            View tarjeta = LayoutInflater.from(this).inflate(
                    R.layout.item_experiencia_laboral,
                    contenedorExperienciasProfesional,
                    false
            );

            // VINCULAR DATOS
            TextView tvPuesto = tarjeta.findViewById(R.id.tvPuestoExperiencia);
            TextView tvEmpresa = tarjeta.findViewById(R.id.tvEmpresaExperiencia);
            TextView tvPeriodo = tarjeta.findViewById(R.id.tvPeriodoExperiencia);
            TextView tvDescripcion = tarjeta.findViewById(R.id.tvDescripcionExperiencia);

            tvPuesto.setText(experiencia.getPuesto());
            tvEmpresa.setText(experiencia.getEmpresa());

            // MOSTRAR PERÍODO
            String periodo = String.valueOf(experiencia.getAnio_inicio());

            if (experiencia.getAnio_fin() != null) {
                periodo += " - " + experiencia.getAnio_fin();
            } else {
                periodo += " - Actualidad";
            }

            tvPeriodo.setText(periodo);
            tvDescripcion.setText(experiencia.getDescripcion());

            // OCULTAR BOTONES DE EDICIÓN Y ELIMINACIÓN
            View btnEditar = tarjeta.findViewById(R.id.btnEditarExperiencia);
            View btnEliminar = tarjeta.findViewById(R.id.btnEliminarExperiencia);

            if (btnEditar != null) {
                btnEditar.setVisibility(View.GONE);
            }

            if (btnEliminar != null) {
                btnEliminar.setVisibility(View.GONE);
            }

            // AGREGAR TARJETA
            contenedorExperienciasProfesional.addView(tarjeta);
        }
    }
}
