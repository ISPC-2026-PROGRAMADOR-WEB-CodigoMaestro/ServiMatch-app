
package com.ispc.servimatch;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Oficio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOficiosActivity extends AppCompatActivity {

    // COMPONENTES
    private EditText edtNombreOficio;
    private Button btnGuardarOficio;
    private Button btnVolverOficios;
    private LinearLayout contenedorOficios;

    // NAVEGACIÓN
    private LinearLayout navInicioAdmin;
    private LinearLayout navUsuariosAdmin;
    private LinearLayout navPerfilAdmin;
    private LinearLayout navSalirAdmin;

    // API Y SESIÓN
    private ApiService apiService;
    private String token;

    private boolean guardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_oficios);

        edtNombreOficio =
                findViewById(R.id.edtNombreOficioAdmin);

        btnGuardarOficio =
                findViewById(R.id.btnGuardarOficioAdmin);

        btnVolverOficios =
                findViewById(R.id.btnVolverOficiosAdmin);

        contenedorOficios =
                findViewById(R.id.contenedorOficiosAdmin);

        navInicioAdmin = findViewById(R.id.navInicioAdmin);
        navUsuariosAdmin = findViewById(R.id.navUsuariosAdmin);
        navPerfilAdmin = findViewById(R.id.navPerfilAdmin);
        navSalirAdmin = findViewById(R.id.navSalirAdmin);

        SharedPreferences preferencias =
                getSharedPreferences(
                        "ServiMatchSesion",
                        MODE_PRIVATE
                );

        token = preferencias.getString(
                "access_token",
                null
        );

        int idRol = preferencias.getInt(
                "id_rol",
                -1
        );

        if (idRol != 10 || token == null || token.isEmpty()) {
            mostrarMensaje("Acceso permitido solo a administradores.");
            finish();
            return;
        }

        apiService = RetrofitClient
                .getClient(AdminOficiosActivity.this)
                .create(ApiService.class);

        btnGuardarOficio.setOnClickListener(
                v -> guardarOficio()
        );

        btnVolverOficios.setOnClickListener(
                v -> finish()
        );

        configurarNavegacion();
        cargarOficios();
    }

    // =====================================
    // CONSULTAR OFICIOS
    // =====================================
    private void cargarOficios() {

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

                            mostrarOficios(response.body());

                        } else {
                            mostrarMensaje(
                                    "Error al cargar oficios: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Oficio>> call,
                            Throwable t) {

                        if (!isFinishing()) {
                            mostrarMensaje(
                                    "Error de conexión al cargar oficios."
                            );
                        }
                    }
                }
        );
    }

    // =====================================
    // MOSTRAR OFICIOS CON BOTONES
    // =====================================
    private void mostrarOficios(List<Oficio> oficios) {

        contenedorOficios.removeAllViews();

        if (oficios.isEmpty()) {

            TextView mensaje = new TextView(this);
            mensaje.setText("No hay oficios registrados.");
            mensaje.setTextSize(16);

            contenedorOficios.addView(mensaje);
            return;
        }

        for (Oficio oficio : oficios) {

            LinearLayout tarjeta = new LinearLayout(this);
            tarjeta.setOrientation(LinearLayout.VERTICAL);

            tarjeta.setPadding(
                    dp(16), dp(12), dp(16), dp(12)
            );

            LinearLayout.LayoutParams tarjetaParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            tarjetaParams.bottomMargin = dp(12);
            tarjeta.setLayoutParams(tarjetaParams);

            tarjeta.setBackgroundResource(
                    R.drawable.card_oficio
            );

            // NOMBRE DEL OFICIO
            TextView txtNombre = new TextView(this);

            txtNombre.setText(
                    oficio.getNombre_oficio()
            );

            txtNombre.setTextSize(18);
            txtNombre.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            tarjeta.addView(txtNombre);

            // CONTENEDOR DE BOTONES
            LinearLayout filaBotones = new LinearLayout(this);
            filaBotones.setOrientation(
                    LinearLayout.HORIZONTAL
            );

            filaBotones.setPadding(
                    0, dp(12), 0, 0
            );

            // BOTÓN EDITAR
            Button btnEditar = new Button(this);
            btnEditar.setText("Editar");
            btnEditar.setAllCaps(false);

            // BOTÓN ELIMINAR
            Button btnEliminar = new Button(this);
            btnEliminar.setText("Eliminar");
            btnEliminar.setAllCaps(false);

            LinearLayout.LayoutParams paramsEditar =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );

            paramsEditar.rightMargin = dp(8);

            LinearLayout.LayoutParams paramsEliminar =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );

            filaBotones.addView(
                    btnEditar,
                    paramsEditar
            );

            filaBotones.addView(
                    btnEliminar,
                    paramsEliminar
            );

            tarjeta.addView(filaBotones);

            contenedorOficios.addView(tarjeta);

            // EVENTOS
            btnEditar.setOnClickListener(
                    v -> mostrarDialogoEditar(oficio)
            );

            btnEliminar.setOnClickListener(
                    v -> confirmarEliminacion(oficio)
            );
        }
    }

    // =====================================
    // REGISTRAR OFICIO
    // =====================================
    private void guardarOficio() {

        if (guardando) {
            return;
        }

        String nombre = edtNombreOficio
                .getText()
                .toString()
                .trim();

        if (nombre.isEmpty()) {
            edtNombreOficio.setError(
                    "Ingresá el nombre del oficio."
            );
            edtNombreOficio.requestFocus();
            return;
        }

        if (nombre.length() < 3) {
            edtNombreOficio.setError(
                    "El nombre debe tener al menos 3 caracteres."
            );
            return;
        }

        Map<String, String> datos = new HashMap<>();
        datos.put("nombre_oficio", nombre);

        guardando = true;
        btnGuardarOficio.setEnabled(false);

        apiService.registrarOficio(
                "Bearer " + token,
                datos
        ).enqueue(new Callback<Oficio>() {

            @Override
            public void onResponse(
                    Call<Oficio> call,
                    Response<Oficio> response) {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                guardando = false;
                btnGuardarOficio.setEnabled(true);

                if (response.isSuccessful()) {

                    mostrarMensaje(
                            "Oficio registrado correctamente."
                    );

                    edtNombreOficio.setText("");
                    cargarOficios();

                } else {
                    mostrarError(
                            response.code(),
                            "registrar"
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<Oficio> call,
                    Throwable t) {

                if (isFinishing()) {
                    return;
                }

                guardando = false;
                btnGuardarOficio.setEnabled(true);

                mostrarMensaje(
                        "Error de conexión con el servidor."
                );
            }
        });
    }

    // =====================================
    // MOSTRAR DIÁLOGO PARA EDITAR
    // =====================================
    private void mostrarDialogoEditar(Oficio oficio) {

        EditText input = new EditText(this);

        input.setText(
                oficio.getNombre_oficio()
        );

        input.setSingleLine(true);

        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setPadding(
                dp(20), dp(8), dp(20), 0
        );

        contenedor.addView(
                input,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Modificar oficio")
                .setMessage("Ingresá el nuevo nombre del oficio.")
                .setView(contenedor)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .create();

        dialogo.setOnShowListener(d -> {

            dialogo.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String nuevoNombre = input
                        .getText()
                        .toString()
                        .trim();

                if (nuevoNombre.length() < 3) {
                    input.setError(
                            "Ingresá al menos 3 caracteres."
                    );
                    return;
                }

                if (nuevoNombre.equals(
                        oficio.getNombre_oficio()
                )) {
                    dialogo.dismiss();
                    return;
                }

                dialogo.getButton(
                        AlertDialog.BUTTON_POSITIVE
                ).setEnabled(false);

                actualizarOficio(
                        oficio.getId(),
                        nuevoNombre,
                        dialogo
                );
            });
        });

        dialogo.show();
    }

    // =====================================
    // MODIFICAR OFICIO
    // =====================================
    private void actualizarOficio(
            int id,
            String nombre,
            AlertDialog dialogo) {

        Map<String, String> datos = new HashMap<>();
        datos.put("nombre_oficio", nombre);

        apiService.actualizarOficio(
                id,
                "Bearer " + token,
                datos
        ).enqueue(new Callback<Oficio>() {

            @Override
            public void onResponse(
                    Call<Oficio> call,
                    Response<Oficio> response) {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (response.isSuccessful()) {

                    dialogo.dismiss();

                    mostrarMensaje(
                            "Oficio modificado correctamente."
                    );

                    cargarOficios();

                } else {

                    dialogo.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setEnabled(true);

                    mostrarError(
                            response.code(),
                            "modificar"
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<Oficio> call,
                    Throwable t) {

                if (isFinishing()) {
                    return;
                }

                dialogo.getButton(
                        AlertDialog.BUTTON_POSITIVE
                ).setEnabled(true);

                mostrarMensaje(
                        "Error de conexión al modificar oficio."
                );
            }
        });
    }

    // =====================================
    // CONFIRMAR ELIMINACIÓN
    // =====================================
    private void confirmarEliminacion(Oficio oficio) {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar oficio")
                .setMessage(
                        "¿Seguro que querés eliminar el oficio \""
                                + oficio.getNombre_oficio()
                                + "\"?"
                )
                .setPositiveButton(
                        "Eliminar",
                        (dialog, which) ->
                                eliminarOficio(oficio.getId())
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =====================================
    // ELIMINAR OFICIO
    // =====================================
    private void eliminarOficio(int id) {

        apiService.eliminarOficio(
                id,
                "Bearer " + token
        ).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response) {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (response.isSuccessful()) {

                    mostrarMensaje(
                            "Oficio eliminado correctamente."
                    );

                    cargarOficios();

                } else if (response.code() == 500) {

                    mostrarMensaje(
                            "No se pudo eliminar el oficio. "
                                    + "Puede estar asociado a usuarios."
                    );

                } else {

                    mostrarError(
                            response.code(),
                            "eliminar"
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<Void> call,
                    Throwable t) {

                if (!isFinishing()) {
                    mostrarMensaje(
                            "Error de conexión al eliminar oficio."
                    );
                }
            }
        });
    }

    // =====================================
    // MANEJAR ERRORES
    // =====================================
    private void mostrarError(
            int codigo,
            String accion) {

        if (codigo == 400) {

            mostrarMensaje(
                    "Datos inválidos o nombre de oficio duplicado."
            );

        } else if (codigo == 401) {

            mostrarMensaje(
                    "Tu sesión expiró. Iniciá sesión nuevamente."
            );

        } else if (codigo == 403) {

            mostrarMensaje(
                    "No tenés permisos para " + accion + " oficios."
            );

        } else if (codigo == 404) {

            mostrarMensaje(
                    "El oficio no fue encontrado."
            );

        } else {

            mostrarMensaje(
                    "Error al " + accion + " oficio. Código: "
                            + codigo
            );
        }
    }

    // =====================================
    // NAVEGACIÓN
    // =====================================
    private void configurarNavegacion() {

        navInicioAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    this,
                    AdminDashboardActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            );

            startActivity(intent);
            finish();
        });

        navUsuariosAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    this,
                    AdminUsuariosActivity.class
            );

            startActivity(intent);
            finish();
        });

        navPerfilAdmin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });

        navSalirAdmin.setOnClickListener(
                v -> cerrarSesion()
        );
    }

    // =====================================
    // CERRAR SESIÓN
    // =====================================
    private void cerrarSesion() {

        SharedPreferences sesion =
                getSharedPreferences(
                        "ServiMatchSesion",
                        MODE_PRIVATE
                );

        sesion.edit().clear().apply();

        SharedPreferences preferencias =
                getSharedPreferences(
                        "ServiMatchPrefs",
                        MODE_PRIVATE
                );

        preferencias.edit().clear().apply();

        Intent intent = new Intent(
                this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    // =====================================
    // CONVERTIR DP A PÍXELES
    // =====================================
    private int dp(int valor) {

        return (int) (
                valor * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    // =====================================
    // MENSAJES
    // =====================================
    private void mostrarMensaje(String mensaje) {

        Toast.makeText(
                this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }
}
