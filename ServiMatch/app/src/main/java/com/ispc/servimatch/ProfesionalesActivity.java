package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.content.Intent;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Ubicacion;
import com.ispc.servimatch.model.Usuario;
import com.ispc.servimatch.model.Favorito;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfesionalesActivity extends AppCompatActivity {

    private TextView tvTituloOficio;
    private TextView tvSinProfesionales;
    private LinearLayout contenedorProfesionales;
    private String oficioSeleccionado;
    private final Set<Integer> favoritosGuardados = new HashSet<>();
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesionales);

        apiService = RetrofitClient.getClient(this)
                .create(ApiService.class);

        SharedPreferences preferencias = getSharedPreferences(
                "ServiMatchSesion",
                MODE_PRIVATE
        );

        token = preferencias.getString("access_token", null);

        tvTituloOficio = findViewById(R.id.tvTituloOficio);
        tvSinProfesionales = findViewById(R.id.tvSinProfesionales);
        contenedorProfesionales = findViewById(R.id.contenedorProfesionales);


        // RECUPERAR DATOS DEL USUARIO
        String usuario = getIntent().getStringExtra("usuario");
        int idUsuario = preferencias.getInt("id_usuario", -1);
        int idRol = preferencias.getInt("id_rol", -1);
        String rol = getIntent().getStringExtra("rol");

        // NAVEGACIÓN - INICIO
        findViewById(R.id.navInicio).setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);
            startActivity(intent);
            finish();
        });

        // NAVEGACIÓN - CONTACTO
        findViewById(R.id.navContacto).setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactoActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);
            startActivity(intent);
            finish();
        });

        // NAVEGACIÓN - GUARDADOS
        findViewById(R.id.navGuardados).setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoritosActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);
            startActivity(intent);
            finish();
        });

        // NAVEGACIÓN - PERFIL
        findViewById(R.id.navPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);
            startActivity(intent);
            finish();
        });

        oficioSeleccionado =
                getIntent().getStringExtra("oficio");

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
                RetrofitClient.getClient(ProfesionalesActivity.this)
                        .create(ApiService.class);

        apiService.obtenerOficios().enqueue(
                new Callback<List<Oficio>>() {

                    @Override
                    public void onResponse(
                            Call<List<Oficio>> call,
                            Response<List<Oficio>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Integer idOficio = null;

                            for (Oficio oficio : response.body()) {

                                if (oficio.getNombre_oficio()
                                        .equalsIgnoreCase(
                                                oficioSeleccionado
                                        )) {

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

    // CONSULTAR PROFESIONALES CON JWT
    private void buscarProfesionales(int idOficio) {

        SharedPreferences preferencias =
                getSharedPreferences(
                        "ServiMatchSesion",
                        MODE_PRIVATE
                );

        String token = preferencias.getString(
                "access_token",
                null
        );

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
                RetrofitClient.getClient(ProfesionalesActivity.this)
                        .create(ApiService.class);

        // ENDPOINT PUBLICADO PARA USUARIOS AUTENTICADOS
        apiService.obtenerProfesionales(
                "Bearer " + token
        ).enqueue(new Callback<List<Usuario>>() {

            @Override
            public void onResponse(
                    Call<List<Usuario>> call,
                    Response<List<Usuario>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    List<Usuario> profesionales = response.body();

                    obtenerFavoritos(() ->
                            buscarUbicaciones(idOficio, profesionales)
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

    // CONSULTAR UBICACIONES
    private void buscarUbicaciones(
            int idOficio,
            List<Usuario> usuarios) {

        ApiService apiService =
                RetrofitClient.getClient(ProfesionalesActivity.this)
                        .create(ApiService.class);

        apiService.obtenerUbicaciones().enqueue(
                new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            mostrarProfesionales(idOficio, usuarios, response.body());

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


    // OBTENER FAVORITOS DEL USUARIO
    private void obtenerFavoritos(Runnable alFinalizar) {

        apiService.obtenerFavoritos("Bearer " + token)
                .enqueue(new Callback<List<Favorito>>() {

                    @Override
                    public void onResponse(
                            Call<List<Favorito>> call,
                            Response<List<Favorito>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            favoritosGuardados.clear();

                            for (Favorito favorito : response.body()) {
                                favoritosGuardados.add(favorito.getProfesional());
                            }
                        } else {
                            Toast.makeText(
                                    ProfesionalesActivity.this,
                                    "No se pudieron cargar los favoritos",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        alFinalizar.run();
                    }

                    @Override
                    public void onFailure(Call<List<Favorito>> call, Throwable t) {

                        Toast.makeText(
                                ProfesionalesActivity.this,
                                "Error de conexión al obtener favoritos",
                                Toast.LENGTH_SHORT
                        ).show();

                        alFinalizar.run();
                    }
                });
    }

    // MOSTRAR PROFESIONALES DEL OFICIO SELECCIONADO
    private void mostrarProfesionales(
            int idOficio,
            List<Usuario> usuarios,
            List<Ubicacion> ubicaciones) {

        boolean encontrado = false;

        contenedorProfesionales.removeAllViews();

        for (Usuario usuario : usuarios) {

            if (usuario.getOficio() != null
                    && usuario.getOficio().intValue() == idOficio) {

                encontrado = true;

                String nombreUbicacion =
                        "Ubicación no disponible";

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

                // CREAR TARJETA
                View card = getLayoutInflater().inflate(
                        R.layout.item_profesional,
                        contenedorProfesionales,
                        false
                );

                ImageView imgFavorito =
                        card.findViewById(R.id.imgFavorito);

                int idProfesional = usuario.getId();

                actualizarIconoFavorito(imgFavorito, idProfesional);

                imgFavorito.setOnClickListener(v -> {
                    cambiarFavorito(idProfesional, imgFavorito);
                });

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



                tvNombreProfesional.setText(
                        usuario.getNombre()
                );

                tvOficioProfesional.setText(
                        oficioSeleccionado
                );

                tvUbicacionProfesional.setText(
                        nombreUbicacion
                );

                colocarIconoOficio(
                        tvIconoOficio,
                        oficioSeleccionado
                );

                contenedorProfesionales.addView(card);
            }
        }

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

    // ICONO SEGÚN OFICIO
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


    // ACTUALIZAR ÍCONO DE FAVORITOS
    private void actualizarIconoFavorito(ImageView imagen, int idProfesional) {

        if (favoritosGuardados.contains(idProfesional)) {
            imagen.setImageResource(R.drawable.ic_bookmark);
            imagen.setContentDescription("Quitar de favoritos");
        } else {
            imagen.setImageResource(R.drawable.ic_bookmark_border);
            imagen.setContentDescription("Agregar a favoritos");
        }
    }

    // AGREGAR O ELIMINAR FAVORITO
    private void cambiarFavorito(int idProfesional, ImageView imagen) {

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Iniciá sesión nuevamente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagen.getTag() != null) {
            return;
        }

        imagen.setTag(true);

        if (favoritosGuardados.contains(idProfesional)) {

            apiService.eliminarFavorito(idProfesional, "Bearer " + token)
                    .enqueue(new Callback<Void>() {

                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            imagen.setTag(null);

                            if (response.isSuccessful()) {
                                favoritosGuardados.remove(idProfesional);
                                actualizarIconoFavorito(imagen, idProfesional);
                            } else {
                                Toast.makeText(ProfesionalesActivity.this,
                                        "No se pudo eliminar el favorito",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            imagen.setTag(null);
                            Toast.makeText(ProfesionalesActivity.this,
                                    "Error de conexión",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {

            Map<String, Integer> datos = new HashMap<>();
            datos.put("profesional", idProfesional);

            apiService.agregarFavorito("Bearer " + token, datos)
                    .enqueue(new Callback<Favorito>() {

                        @Override
                        public void onResponse(
                                Call<Favorito> call,
                                Response<Favorito> response) {

                            imagen.setTag(null);

                            if (response.isSuccessful()) {
                                favoritosGuardados.add(idProfesional);
                                actualizarIconoFavorito(imagen, idProfesional);
                            } else {
                                Toast.makeText(ProfesionalesActivity.this,
                                        "No se pudo guardar el favorito",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Favorito> call, Throwable t) {
                            imagen.setTag(null);
                            Toast.makeText(ProfesionalesActivity.this,
                                    "Error de conexión",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
