package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Ubicacion;
import com.ispc.servimatch.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfesionalesActivity extends AppCompatActivity {

    private TextView tvTituloOficio;
    private TextView tvSinProfesionales;
    private LinearLayout contenedorProfesionales;

    private String oficioSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesionales);

        // =========================================
        // VINCULAR ELEMENTOS DEL XML
        // =========================================

        tvTituloOficio =
                findViewById(R.id.tvTituloOficio);

        tvSinProfesionales =
                findViewById(R.id.tvSinProfesionales);

        contenedorProfesionales =
                findViewById(R.id.contenedorProfesionales);

        // =========================================
        // RECIBIR OFICIO DESDE EL DASHBOARD
        // =========================================

        oficioSeleccionado =
                getIntent().getStringExtra("oficio");

        // =========================================
        // MOSTRAR OFICIO SELECCIONADO
        // =========================================

        if (oficioSeleccionado != null
                && !oficioSeleccionado.isEmpty()) {

            tvTituloOficio.setText(
                    "Profesionales - " + oficioSeleccionado
            );

            buscarOficio();

        } else {

            tvTituloOficio.setText("Profesionales");

            tvSinProfesionales.setText(
                    "No se seleccionó ningún oficio."
            );

            tvSinProfesionales.setVisibility(
                    View.VISIBLE
            );
        }
    }

    // =============================================
    // BUSCAR EL ID DEL OFICIO
    // =============================================

    private void buscarOficio() {

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        apiService.obtenerOficios().enqueue(
                new Callback<List<Oficio>>() {

                    @Override
                    public void onResponse(
                            Call<List<Oficio>> call,
                            Response<List<Oficio>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Oficio> oficios =
                                    response.body();

                            Integer idOficio = null;

                            for (Oficio oficio : oficios) {

                                if (oficio.getNombre_oficio()
                                        .equalsIgnoreCase(
                                                oficioSeleccionado)) {

                                    idOficio =
                                            oficio.getId();

                                    break;
                                }
                            }

                            if (idOficio != null) {

                                buscarProfesionales(idOficio);

                            } else {

                                tvSinProfesionales.setText(
                                        "El oficio seleccionado no está registrado."
                                );

                                tvSinProfesionales.setVisibility(
                                        View.VISIBLE
                                );
                            }

                        } else {

                            Toast.makeText(
                                    ProfesionalesActivity.this,
                                    "Error al obtener los oficios",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Oficio>> call,
                            Throwable t) {

                        Toast.makeText(
                                ProfesionalesActivity.this,
                                "Error de conexión con el servidor",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =============================================
    // OBTENER USUARIOS
    // =============================================

    private void buscarProfesionales(int idOficio) {

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        apiService.obtenerUsuarios().enqueue(
                new Callback<List<Usuario>>() {

                    @Override
                    public void onResponse(
                            Call<List<Usuario>> call,
                            Response<List<Usuario>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Usuario> usuarios =
                                    response.body();

                            // Una vez obtenidos los usuarios,
                            // obtenemos las ubicaciones.
                            buscarUbicaciones(
                                    idOficio,
                                    usuarios
                            );

                        } else {

                            Toast.makeText(
                                    ProfesionalesActivity.this,
                                    "Error al obtener los profesionales",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Usuario>> call,
                            Throwable t) {

                        Toast.makeText(
                                ProfesionalesActivity.this,
                                "Error de conexión con el servidor",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =============================================
    // OBTENER UBICACIONES
    // =============================================

    private void buscarUbicaciones(
            int idOficio,
            List<Usuario> usuarios) {

        ApiService apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);

        apiService.obtenerUbicaciones().enqueue(
                new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Ubicacion> ubicaciones =
                                    response.body();

                            mostrarProfesionales(
                                    idOficio,
                                    usuarios,
                                    ubicaciones
                            );

                        } else {

                            Toast.makeText(
                                    ProfesionalesActivity.this,
                                    "Error al obtener las ubicaciones",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        Toast.makeText(
                                ProfesionalesActivity.this,
                                "Error de conexión con el servidor",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =============================================
    // MOSTRAR PROFESIONALES
    // =============================================

    private void mostrarProfesionales(
            int idOficio,
            List<Usuario> usuarios,
            List<Ubicacion> ubicaciones) {

        boolean encontrado = false;

        contenedorProfesionales.removeAllViews();

        for (Usuario usuario : usuarios) {

            // Rol 11 = Estándar
            // También debe coincidir el oficio.
            if (usuario.getRol() != null
                    && usuario.getOficio() != null
                    && usuario.getRol().intValue() == 11
                    && usuario.getOficio().intValue() == idOficio) {

                encontrado = true;

                String nombreUbicacion =
                        "Ubicación no disponible";

                // =================================
                // BUSCAR UBICACIÓN DEL USUARIO
                // =================================

                if (usuario.getUbicacion() != null) {

                    for (Ubicacion ubicacion : ubicaciones) {

                        if (ubicacion.getId()
                                == usuario.getUbicacion().intValue()) {

                            nombreUbicacion =
                                    ubicacion.getCiudad()
                                            + ", "
                                            + ubicacion.getProvincia();

                            break;
                        }
                    }
                }

                // =================================
                // CREAR CARD
                // =================================

                LinearLayout card =
                        new LinearLayout(
                                ProfesionalesActivity.this
                        );

                card.setOrientation(
                        LinearLayout.VERTICAL
                );

                card.setPadding(
                        32,
                        28,
                        32,
                        28
                );

                // Fondo de la card
                card.setBackgroundResource(
                        R.drawable.card_oficio
                );

                // =================================
                // NOMBRE
                // =================================

                TextView tvNombre =
                        new TextView(
                                ProfesionalesActivity.this
                        );

                tvNombre.setText(
                        usuario.getNombre()
                );

                tvNombre.setTextSize(19);

                tvNombre.setTextColor(
                        getResources().getColor(
                                R.color.texto_principal
                        )
                );

                // =================================
                // OFICIO
                // =================================

                TextView tvOficio =
                        new TextView(
                                ProfesionalesActivity.this
                        );

                tvOficio.setText(
                        oficioSeleccionado
                );

                tvOficio.setTextSize(16);

                tvOficio.setTextColor(
                        getResources().getColor(
                                R.color.color_primario
                        )
                );

                // =================================
                // UBICACIÓN
                // =================================

                TextView tvUbicacion =
                        new TextView(
                                ProfesionalesActivity.this
                        );

                tvUbicacion.setText(
                        nombreUbicacion
                );

                tvUbicacion.setTextSize(15);

                tvUbicacion.setTextColor(
                        getResources().getColor(
                                R.color.texto_secundario
                        )
                );

                // =================================
                // AGREGAR DATOS A LA CARD
                // =================================

                card.addView(tvNombre);
                card.addView(tvOficio);
                card.addView(tvUbicacion);

                // =================================
                // MARGEN ENTRE CARDS
                // =================================

                LinearLayout.LayoutParams parametros =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                parametros.setMargins(
                        0,
                        0,
                        0,
                        24
                );

                card.setLayoutParams(parametros);

                // =================================
                // AGREGAR CARD A LA PANTALLA
                // =================================

                contenedorProfesionales.addView(
                        card
                );
            }
        }

        // =========================================
        // SIN PROFESIONALES
        // =========================================

        if (!encontrado) {

            tvSinProfesionales.setText(
                    "No hay profesionales disponibles para "
                            + oficioSeleccionado
                            + "."
            );

            tvSinProfesionales.setVisibility(
                    View.VISIBLE
            );

        } else {

            tvSinProfesionales.setVisibility(
                    View.GONE
            );
        }
    }
}