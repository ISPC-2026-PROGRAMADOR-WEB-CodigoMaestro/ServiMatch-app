
package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Oficio;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtBienvenida;

    // CONTENEDOR DINÁMICO DE OFICIOS
    private GridLayout gridOficios;

    // BARRA DE NAVEGACIÓN
    private LinearLayout navInicio;
    private LinearLayout navContacto;
    private LinearLayout navPerfil;

    // ELEMENTOS VISUALES DE INICIO
    private TextView iconInicio;
    private TextView textoInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // VINCULAR ELEMENTOS DEL DASHBOARD
        txtBienvenida = findViewById(R.id.txtBienvenida);
        gridOficios = findViewById(R.id.gridOficios);

        // RECIBIR DATOS DEL USUARIO DESDE LOGIN
        String usuario = getIntent().getStringExtra("usuario");
        int idUsuario = getIntent().getIntExtra("id_usuario", -1);
        int idRol = getIntent().getIntExtra("id_rol", -1);
        String rol = getIntent().getStringExtra("rol");

        // MOSTRAR BIENVENIDA
        if (usuario != null && !usuario.isEmpty()) {
            txtBienvenida.setText("¡Hola, " + usuario + "!");
        } else {
            txtBienvenida.setText("¡Hola!");
        }

        // BARRA DE NAVEGACIÓN
        navInicio = findViewById(R.id.navInicio);
        navContacto = findViewById(R.id.navContacto);
        navPerfil = findViewById(R.id.navPerfil);
        iconInicio = findViewById(R.id.iconInicio);
        textoInicio = findViewById(R.id.textoInicio);

        // MARCAR INICIO COMO SELECCIONADO
        iconInicio.setTextColor(
                getResources().getColor(R.color.color_primario)
        );

        textoInicio.setTextColor(
                getResources().getColor(R.color.color_primario)
        );

        textoInicio.setTypeface(null, Typeface.BOLD);

        // NAVEGACIÓN - INICIO
        navInicio.setOnClickListener(v ->
                Toast.makeText(
                        DashboardActivity.this,
                        "Ya estás en Inicio",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // NAVEGACIÓN - CONTACTO
        navContacto.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    ContactoActivity.class
            );

            if (usuario != null) {
                intent.putExtra("usuario", usuario);
            }

            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);

            if (rol != null) {
                intent.putExtra("rol", rol);
            }

            startActivity(intent);
        });

        // NAVEGACIÓN - PERFIL
        navPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    PerfilActivity.class
            );

            if (usuario != null) {
                intent.putExtra("usuario", usuario);
            }

            intent.putExtra("id_usuario", idUsuario);
            intent.putExtra("id_rol", idRol);

            if (rol != null) {
                intent.putExtra("rol", rol);
            }

            startActivity(intent);
        });

        // CONSULTAR OFICIOS REGISTRADOS
        cargarOficios();
    }

    // =====================================
    // OBTENER OFICIOS DESDE DJANGO
    // =====================================
    private void cargarOficios() {

        ApiService apiService = RetrofitClient
                .getClient(DashboardActivity.this)
                .create(ApiService.class);

        apiService.obtenerOficios().enqueue(
                new Callback<List<Oficio>>() {

                    @Override
                    public void onResponse(
                            Call<List<Oficio>> call,
                            Response<List<Oficio>> response) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {

                            gridOficios.removeAllViews();

                            for (Oficio oficio : response.body()) {

                                if (oficio != null
                                        && oficio.getNombre_oficio() != null) {

                                    crearTarjetaOficio(oficio);
                                }
                            }

                        } else {
                            Toast.makeText(
                                    DashboardActivity.this,
                                    "Error al obtener los oficios",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Oficio>> call,
                            Throwable t) {

                        if (!isFinishing() && !isDestroyed()) {
                            Toast.makeText(
                                    DashboardActivity.this,
                                    "Error de conexión con el servidor",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }
        );
    }

    // =====================================
    // CREAR TARJETA DINÁMICA DE OFICIO
    // =====================================
    private void crearTarjetaOficio(Oficio oficio) {

        float densidad =
                getResources().getDisplayMetrics().density;

        int padding = (int) (12 * densidad + 0.5f);
        int margen = (int) (6 * densidad + 0.5f);
        int alto = (int) (140 * densidad + 0.5f);
        int margenSuperiorNombre =
                (int) (8 * densidad + 0.5f);

        // CREAR TARJETA
        LinearLayout tarjeta = new LinearLayout(this);

        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setGravity(Gravity.CENTER);

        tarjeta.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        tarjeta.setBackgroundResource(
                R.drawable.card_oficio
        );

        tarjeta.setElevation(4 * densidad);
        tarjeta.setClickable(true);
        tarjeta.setFocusable(true);

        // CONFIGURAR DISTRIBUCIÓN
        GridLayout.LayoutParams parametros =
                new GridLayout.LayoutParams();

        parametros.width = 0;
        parametros.height = alto;

        parametros.columnSpec = GridLayout.spec(
                GridLayout.UNDEFINED,
                1f
        );

        parametros.setMargins(
                margen,
                margen,
                margen,
                margen
        );

        tarjeta.setLayoutParams(parametros);

        // =====================================
        // ICONO DEL OFICIO
        // =====================================
        TextView icono = new TextView(this);

        // OBTENER ICONO GUARDADO EN DJANGO
        String iconoSeleccionado = oficio.getIcono();

        // COMPATIBILIDAD CON OFICIOS ANTERIORES
        if (iconoSeleccionado == null
                || iconoSeleccionado.trim().isEmpty()
                || iconoSeleccionado.equals("🛠")) {

            iconoSeleccionado = obtenerIconoOficio(
                    oficio.getNombre_oficio()
            );
        }

        icono.setText(iconoSeleccionado);
        icono.setTextSize(42);
        icono.setGravity(Gravity.CENTER);

        // =====================================
        // NOMBRE DEL OFICIO
        // =====================================
        TextView nombre = new TextView(this);

        nombre.setText(oficio.getNombre_oficio());
        nombre.setTextSize(17);

        nombre.setTextColor(
                getResources().getColor(
                        R.color.texto_principal
                )
        );

        nombre.setTypeface(null, Typeface.BOLD);
        nombre.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams parametrosNombre =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosNombre.topMargin = margenSuperiorNombre;
        nombre.setLayoutParams(parametrosNombre);

        // AGREGAR ELEMENTOS A LA TARJETA
        tarjeta.addView(icono);
        tarjeta.addView(nombre);

        // ABRIR PROFESIONALES DEL OFICIO
        tarjeta.setOnClickListener(v ->
                abrirProfesionales(
                        oficio.getNombre_oficio()
                )
        );

        // AGREGAR TARJETA AL DASHBOARD
        gridOficios.addView(tarjeta);
    }

    // =====================================
    // ICONOS DE OFICIOS ANTERIORES
    // =====================================
    private String obtenerIconoOficio(String oficio) {

        if (oficio == null) {
            return "🛠";
        }

        switch (oficio.trim().toLowerCase(Locale.ROOT)) {

            case "electricista":
                return "⚡";

            case "plomero":
                return "🚰";

            case "gasista":
                return "🔥";

            case "carpintero":
                return "🔨";

            case "pintor":
                return "🎨";

            case "jardinero":
                return "🌱";

            case "cerrajero":
                return "🔑";

            case "mecánico":
            case "mecanico":
                return "🔧";

            default:
                return "🛠";
        }
    }

    // =====================================
    // ABRIR LISTADO DE PROFESIONALES
    // =====================================
    private void abrirProfesionales(String oficio) {

        Intent intent = new Intent(
                DashboardActivity.this,
                ProfesionalesActivity.class
        );

        intent.putExtra("oficio", oficio);

        startActivity(intent);
    }
}
