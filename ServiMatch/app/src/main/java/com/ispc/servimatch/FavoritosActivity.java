package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.ColorStateList;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Favorito;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Ubicacion;
import com.ispc.servimatch.model.Usuario;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritosActivity extends AppCompatActivity {

    private LinearLayout contenedorFavoritos;
    private TextView tvSinFavoritos;
    private ApiService apiService;
    private String token;
    private final Set<Integer> favoritosGuardados = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        SharedPreferences preferencias = getSharedPreferences("ServiMatchSesion", MODE_PRIVATE);
        token = preferencias.getString("access_token", null);

        contenedorFavoritos = findViewById(R.id.contenedorFavoritos);
        tvSinFavoritos = findViewById(R.id.tvSinFavoritos);

        // RECIBIR DATOS DEL USUARIO
        String usuario = getIntent().getStringExtra("usuario");
        int idUsuario = getIntent().getIntExtra("id_usuario", -1);
        int idRol = getIntent().getIntExtra("id_rol", -1);
        String rol = getIntent().getStringExtra("rol");

        // MARCAR GUARDADOS COMO SELECCIONADO
        ImageView iconGuardados = findViewById(R.id.iconGuardados);
        TextView textoGuardados = findViewById(R.id.textoGuardados);

        int colorSeleccionado = getColor(R.color.color_primario);
        iconGuardados.setImageTintList(ColorStateList.valueOf(colorSeleccionado));
        textoGuardados.setTextColor(colorSeleccionado);
        textoGuardados.setTypeface(null, Typeface.BOLD);


        // BARRA DE NAVEGACIÓN

        // INICIO
        findViewById(R.id.navInicio).setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);
            startActivity(intent);
        });

        // CONTACTO
        findViewById(R.id.navContacto).setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactoActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);
            startActivity(intent);
        });

        // GUARDADOS
        findViewById(R.id.navGuardados).setOnClickListener(v ->
                Toast.makeText(this, "Ya estás en Guardados", Toast.LENGTH_SHORT).show());

        // PERFIL
        findViewById(R.id.navPerfil).setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            intent.putExtra("usuario", usuario);
            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);
            intent.putExtra("rol", rol);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (apiService != null) cargarFavoritos();
    }

    // CONSULTAR FAVORITOS DEL USUARIO
    private void cargarFavoritos() {
        if (token == null || token.isEmpty()) {
            tvSinFavoritos.setText("Iniciá sesión para consultar tus favoritos.");
            tvSinFavoritos.setVisibility(View.VISIBLE);
            return;
        }

        apiService.obtenerFavoritos("Bearer " + token).enqueue(new Callback<List<Favorito>>() {
            @Override
            public void onResponse(Call<List<Favorito>> call, Response<List<Favorito>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoritosGuardados.clear();

                    for (Favorito favorito : response.body()) {
                        favoritosGuardados.add(favorito.getProfesional());
                    }

                    if (favoritosGuardados.isEmpty()) {
                        contenedorFavoritos.removeAllViews();
                        tvSinFavoritos.setText("Todavía no guardaste ningún profesional.");
                        tvSinFavoritos.setVisibility(View.VISIBLE);
                    } else {
                        cargarProfesionales();
                    }
                } else {
                    Toast.makeText(FavoritosActivity.this, "No se pudieron cargar los favoritos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Favorito>> call, Throwable t) {
                Toast.makeText(FavoritosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // CONSULTAR PROFESIONALES
    private void cargarProfesionales() {
        apiService.obtenerProfesionales("Bearer " + token).enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Usuario> profesionales = response.body();
                    cargarOficios(profesionales);
                } else {
                    Toast.makeText(FavoritosActivity.this, "No se pudieron cargar los profesionales", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(FavoritosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // CONSULTAR OFICIOS
    private void cargarOficios(List<Usuario> profesionales) {
        apiService.obtenerOficios().enqueue(new Callback<List<Oficio>>() {
            @Override
            public void onResponse(Call<List<Oficio>> call, Response<List<Oficio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cargarUbicaciones(profesionales, response.body());
                } else {
                    Toast.makeText(FavoritosActivity.this, "No se pudieron cargar los oficios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Oficio>> call, Throwable t) {
                Toast.makeText(FavoritosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // CONSULTAR UBICACIONES
    private void cargarUbicaciones(List<Usuario> profesionales, List<Oficio> oficios) {
        apiService.obtenerUbicaciones().enqueue(new Callback<List<Ubicacion>>() {
            @Override
            public void onResponse(Call<List<Ubicacion>> call, Response<List<Ubicacion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mostrarFavoritos(profesionales, oficios, response.body());
                } else {
                    Toast.makeText(FavoritosActivity.this, "No se pudieron cargar las ubicaciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ubicacion>> call, Throwable t) {
                Toast.makeText(FavoritosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // MOSTRAR PROFESIONALES GUARDADOS
    private void mostrarFavoritos(List<Usuario> profesionales, List<Oficio> oficios, List<Ubicacion> ubicaciones) {
        contenedorFavoritos.removeAllViews();
        int cantidad = 0;

        for (Usuario usuario : profesionales) {
            if (!favoritosGuardados.contains(usuario.getId())) continue;
            cantidad++;

            String nombreOficio = "Oficio no disponible";
            String nombreUbicacion = "Ubicación no disponible";

            if (usuario.getOficio() != null) {
                for (Oficio oficio : oficios) {
                    if (oficio.getId() == usuario.getOficio().intValue()) {
                        nombreOficio = oficio.getNombre_oficio();
                        break;
                    }
                }
            }

            if (usuario.getUbicacion() != null) {
                for (Ubicacion ubicacion : ubicaciones) {
                    if (ubicacion.getId() == usuario.getUbicacion().intValue()) {
                        nombreUbicacion = ubicacion.getCiudad() + ", " + ubicacion.getProvincia();
                        break;
                    }
                }
            }

            View card = getLayoutInflater().inflate(R.layout.item_profesional, contenedorFavoritos, false);
            TextView tvNombre = card.findViewById(R.id.tvNombreProfesional);
            TextView tvOficio = card.findViewById(R.id.tvOficioProfesional);
            TextView tvUbicacion = card.findViewById(R.id.tvUbicacionProfesional);
            TextView tvIcono = card.findViewById(R.id.tvIconoOficio);
            ImageView imgFavorito = card.findViewById(R.id.imgFavorito);

            tvNombre.setText(usuario.getNombre());
            tvOficio.setText(nombreOficio);
            tvUbicacion.setText(nombreUbicacion);
            tvIcono.setText(obtenerIconoOficio(nombreOficio));
            imgFavorito.setImageResource(R.drawable.ic_bookmark);
            imgFavorito.setContentDescription("Quitar de favoritos");

            int idProfesional = usuario.getId();
            imgFavorito.setOnClickListener(v -> eliminarFavorito(idProfesional, card, imgFavorito));

            contenedorFavoritos.addView(card);
        }

        if (cantidad == 0) {
            tvSinFavoritos.setText("No se encontraron profesionales guardados.");
            tvSinFavoritos.setVisibility(View.VISIBLE);
        } else {
            tvSinFavoritos.setVisibility(View.GONE);
        }
    }

    // ELIMINAR FAVORITO
    private void eliminarFavorito(int idProfesional, View card, ImageView imagen) {
        if (!imagen.isEnabled()) return;
        imagen.setEnabled(false);

        apiService.eliminarFavorito(idProfesional, "Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                imagen.setEnabled(true);

                if (response.isSuccessful()) {
                    favoritosGuardados.remove(idProfesional);
                    contenedorFavoritos.removeView(card);

                    if (contenedorFavoritos.getChildCount() == 0) {
                        tvSinFavoritos.setText("Todavía no guardaste ningún profesional.");
                        tvSinFavoritos.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(FavoritosActivity.this, "No se pudo eliminar el favorito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                imagen.setEnabled(true);
                Toast.makeText(FavoritosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ÍCONO SEGÚN OFICIO
    private String obtenerIconoOficio(String oficio) {
        switch (oficio.toLowerCase()) {
            case "electricista": return "⚡";
            case "plomero": return "💧";
            case "gasista": return "🔥";
            case "carpintero": return "🔨";
            case "pintor": return "🖌";
            case "jardinero": return "🌿";
            case "cerrajero": return "🔑";
            case "mecánico":
            case "mecanico": return "🔧";
            default: return "🛠";
        }
    }
}
