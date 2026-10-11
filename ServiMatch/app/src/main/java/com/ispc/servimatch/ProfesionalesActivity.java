
package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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

        // VINCULAR ELEMENTOS DEL XML
        tvTituloOficio = findViewById(R.id.tvTituloOficio);
        tvSinProfesionales = findViewById(R.id.tvSinProfesionales);
        contenedorProfesionales = findViewById(R.id.contenedorProfesionales);

        // RECIBIR OFICIO DESDE EL DASHBOARD
        oficioSeleccionado = getIntent().getStringExtra("oficio");

        // MOSTRAR OFICIO SELECCIONADO
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

            tvSinProfesionales.setVisibility(View.VISIBLE);
        }
    }

    // BUSCAR EL ID DEL OFICIO
    private void buscarOficio() {

        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        apiService.obtenerOficios().enqueue(
                new Callback<List<Oficio>>() {

                    @Override
                    public void onResponse(
                            Call<List<Oficio>> call,
                            Response<List<Oficio>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Oficio> oficios = response.body();

                            Integer idOficio = null;

                            for (Oficio oficio : oficios) {

                                if (oficio.getNombre_oficio()
                                        .equalsIgnoreCase(oficioSeleccionado)) {

                                    idOficio = oficio.getId();
                                    break;
                                }
                            }

                            if (idOficio != null) {

                                buscarProfesionales(idOficio);

                            } else {

                                tvSinProfesionales.setText(
                                        "El oficio seleccionado no está registrado."
                                );

                                tvSinProfesionales.setVisibility(View.VISIBLE);
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

    // OBTENER PROFESIONALES CON TOKEN JWT
    private void buscarProfesionales(int idOficio) {

        // RECUPERAR TOKEN GUARDADO EN LOGIN
        SharedPreferences preferencias =
                getSharedPreferences(
                        "ServiMatchSesion",
                        MODE_PRIVATE
                );

        String token = preferencias.getString(
                "access_token",
                null
        );

        // VALIDAR SESIÓN
        if (token == null || token.isEmpty()) {

            tvSinProfesionales.setText(
                    "Iniciá sesión para consultar los profesionales."
            );

            tvSinProfesionales.setVisibility(View.VISIBLE);

            Toast.makeText(
                    this,
                    "No hay una sesión activa",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        // ENVIAR TOKEN JWT A DJANGO
        apiService.obtenerUsuarios("Bearer " + token).enqueue(
                new Callback<List<Usuario>>() {

                    @Override
                    public void onResponse(
                            Call<List<Usuario>> call,
                            Response<List<Usuario>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Usuario> usuarios = response.body();

                            buscarUbicaciones(
                                    idOficio,
                                    usuarios
                            );

                        } else if (response.code() == 401) {

                            tvSinProfesionales.setText(
                                    "Tu sesión expiró. Iniciá sesión nuevamente."
                            );

                            tvSinProfesionales.setVisibility(View.VISIBLE);

                        } else if (response.code() == 403) {

                            tvSinProfesionales.setText(
                                    "No tenés permisos para consultar este listado."
                            );

                            tvSinProfesionales.setVisibility(View.VISIBLE);

                        } else {

                            Toast.makeText(
                                    ProfesionalesActivity.this,
                                    "Error al obtener los profesionales. Código: "
                                            + response.code(),
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

    // OBTENER UBICACIONES
    private void buscarUbicaciones(
            int idOficio,
            List<Usuario> usuarios) {

        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        apiService.obtenerUbicaciones().enqueue(
                new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Ubicacion> ubicaciones = response.body();

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

    // MOSTRAR PROFESIONALES
    private void mostrarProfesionales(
            int idOficio,
            List<Usuario> usuarios,
            List<Ubicacion> ubicaciones) {

        boolean encontrado = false;

        contenedorProfesionales.removeAllViews();

        for (Usuario usuario : usuarios) {

            // ROL 11 Y OFICIO COINCIDENTE
            if (usuario.getRol() != null
                    && usuario.getOficio() != null
                    && usuario.getRol().intValue() == 11
                    && usuario.getOficio().intValue() == idOficio) {

                encontrado = true;

                String nombreUbicacion =
                        "Ubicación no disponible";

                // BUSCAR UBICACIÓN
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

                // CARGAR TARJETA
                View card = getLayoutInflater().inflate(
                        R.layout.item_profesional,
                        contenedorProfesionales,
                        false
                );

                TextView tvNombreProfesional =
                        card.findViewById(
                                R.id.tvNombreProfesional
                        );

                TextView tvOficioProfesional =
                        card.findViewById(
                                R.id.tvOficioProfesional
                        );

                TextView tvUbicacionProfesional =
                        card.findViewById(
                                R.id.tvUbicacionProfesional
                        );

                TextView tvIconoOficio =
                        card.findViewById(
                                R.id.tvIconoOficio
                        );

                // DATOS DEL PROFESIONAL
                tvNombreProfesional.setText(
                        usuario.getNombre()
                );

                tvOficioProfesional.setText(
                        oficioSeleccionado
                );

                tvUbicacionProfesional.setText(
                        nombreUbicacion
                );

                // ICONO SEGÚN OFICIO
                colocarIconoOficio(
                        tvIconoOficio,
                        oficioSeleccionado
                );

                // AGREGAR TARJETA
                contenedorProfesionales.addView(card);
            }
        }

        // SIN PROFESIONALES
        if (!encontrado) {

            tvSinProfesionales.setText(
                    "No hay profesionales disponibles para "
                            + oficioSeleccionado + "."
            );

            tvSinProfesionales.setVisibility(View.VISIBLE);

        } else {

            tvSinProfesionales.setVisibility(View.GONE);
        }
    }

    // ICONO DEL OFICIO
    private void colocarIconoOficio(
            TextView tvIconoOficio,
            String oficio) {

        if (oficio == null) {
            tvIconoOficio.setText("🛠");
            return;
        }

        switch (oficio.toLowerCase()) {

            case "electricista":
                tvIconoOficio.setText("⚡");
                break;

            case "plomero":
                tvIconoOficio.setText("💧");
                break;

            case "gasista":
                tvIconoOficio.setText("🔥");
                break;

            case "carpintero":
                tvIconoOficio.setText("🔨");
                break;

            case "pintor":
                tvIconoOficio.setText("🖌");
                break;

            case "jardinero":
                tvIconoOficio.setText("🌿");
                break;

            case "cerrajero":
                tvIconoOficio.setText("🔑");
                break;

            case "mecánico":
            case "mecanico":
                tvIconoOficio.setText("🔧");
                break;

            default:
                tvIconoOficio.setText("🛠");
                break;
        }
    }
}
