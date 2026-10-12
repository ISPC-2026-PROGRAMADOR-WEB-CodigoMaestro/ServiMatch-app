
package com.ispc.servimatch;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Ubicacion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUbicacionesActivity extends AppCompatActivity {

    // FORMULARIO
    private EditText edtCiudad;
    private EditText edtProvincia;
    private Button btnGuardar;
    private Button btnVolver;

    // LISTADO
    private LinearLayout contenedorUbicaciones;

    // NAVEGACIÓN
    private LinearLayout navInicioAdmin;
    private LinearLayout navUsuariosAdmin;
    private LinearLayout navPerfilAdmin;
    private LinearLayout navSalirAdmin;

    // API
    private ApiService apiService;
    private String token;

    private boolean guardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_ubicaciones);

        // VINCULAR FORMULARIO
        edtCiudad = findViewById(R.id.edtCiudadAdmin);
        edtProvincia = findViewById(R.id.edtProvinciaAdmin);

        btnGuardar = findViewById(
                R.id.btnGuardarUbicacionAdmin
        );

        btnVolver = findViewById(
                R.id.btnVolverUbicacionesAdmin
        );

        contenedorUbicaciones = findViewById(
                R.id.contenedorUbicacionesAdmin
        );

        // VINCULAR NAVEGACIÓN
        navInicioAdmin = findViewById(R.id.navInicioAdmin);
        navUsuariosAdmin = findViewById(R.id.navUsuariosAdmin);
        navPerfilAdmin = findViewById(R.id.navPerfilAdmin);
        navSalirAdmin = findViewById(R.id.navSalirAdmin);

        // RECUPERAR SESIÓN
        SharedPreferences sesion = getSharedPreferences(
                "ServiMatchSesion",
                MODE_PRIVATE
        );

        token = sesion.getString("access_token", null);
        int idRol = sesion.getInt("id_rol", -1);

        if (idRol != 10 || token == null || token.isEmpty()) {
            mostrarMensaje("Acceso permitido solo a administradores.");
            finish();
            return;
        }

        // INICIALIZAR RETROFIT
        apiService = RetrofitClient
                .getClient(AdminUbicacionesActivity.this)
                .create(ApiService.class);

        btnGuardar.setOnClickListener(v -> guardarUbicacion());
        btnVolver.setOnClickListener(v -> finish());

        configurarNavegacion();
        cargarUbicaciones();
    }

    // =====================================
    // OBTENER UBICACIONES
    // =====================================
    private void cargarUbicaciones() {

        apiService.obtenerUbicaciones().enqueue(
                new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {

                            mostrarUbicaciones(response.body());

                        } else {
                            mostrarMensaje(
                                    "Error al cargar ubicaciones: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        if (!isFinishing() && !isDestroyed()) {
                            mostrarMensaje(
                                    "Error de conexión al cargar ubicaciones."
                            );
                        }
                    }
                }
        );
    }

    // =====================================
    // MOSTRAR UBICACIONES
    // =====================================
    private void mostrarUbicaciones(List<Ubicacion> ubicaciones) {

        contenedorUbicaciones.removeAllViews();

        if (ubicaciones.isEmpty()) {

            TextView mensaje = new TextView(this);
            mensaje.setText("No hay ubicaciones registradas.");
            mensaje.setTextSize(16);

            contenedorUbicaciones.addView(mensaje);
            return;
        }

        for (Ubicacion ubicacion : ubicaciones) {

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

            tarjeta.setBackgroundResource(R.drawable.card_oficio);

            // CIUDAD
            TextView txtCiudad = new TextView(this);

            txtCiudad.setText(ubicacion.getCiudad());
            txtCiudad.setTextSize(18);
            txtCiudad.setTypeface(null, Typeface.BOLD);

            tarjeta.addView(txtCiudad);

            // PROVINCIA
            TextView txtProvincia = new TextView(this);

            txtProvincia.setText(
                    "Provincia: " + ubicacion.getProvincia()
            );

            txtProvincia.setTextSize(15);

            tarjeta.addView(txtProvincia);

            // BOTONES
            LinearLayout filaBotones = new LinearLayout(this);
            filaBotones.setOrientation(LinearLayout.HORIZONTAL);

            filaBotones.setPadding(0, dp(12), 0, 0);

            Button btnEditar = new Button(this);
            btnEditar.setText("Editar");
            btnEditar.setAllCaps(false);

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

            filaBotones.addView(btnEditar, paramsEditar);
            filaBotones.addView(btnEliminar, paramsEliminar);

            tarjeta.addView(filaBotones);
            contenedorUbicaciones.addView(tarjeta);

            btnEditar.setOnClickListener(
                    v -> mostrarDialogoEditar(ubicacion)
            );

            btnEliminar.setOnClickListener(
                    v -> confirmarEliminacion(ubicacion)
            );
        }
    }

    // =====================================
    // REGISTRAR UBICACIÓN
    // =====================================
    private void guardarUbicacion() {

        if (guardando) {
            return;
        }

        String ciudad = edtCiudad
                .getText().toString().trim();

        String provincia = edtProvincia
                .getText().toString().trim();

        if (!validarDatos(
                ciudad, provincia, edtCiudad, edtProvincia
        )) {
            return;
        }

        Map<String, String> datos = new HashMap<>();
        datos.put("ciudad", ciudad);
        datos.put("provincia", provincia);

        guardando = true;
        btnGuardar.setEnabled(false);

        apiService.registrarUbicacion(
                "Bearer " + token,
                datos
        ).enqueue(new Callback<Ubicacion>() {

            @Override
            public void onResponse(
                    Call<Ubicacion> call,
                    Response<Ubicacion> response) {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                guardando = false;
                btnGuardar.setEnabled(true);

                if (response.isSuccessful()) {

                    mostrarMensaje(
                            "Ubicación registrada correctamente."
                    );

                    edtCiudad.setText("");
                    edtProvincia.setText("");

                    cargarUbicaciones();

                } else {
                    mostrarError(response.code(), "registrar");
                }
            }

            @Override
            public void onFailure(
                    Call<Ubicacion> call,
                    Throwable t) {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                guardando = false;
                btnGuardar.setEnabled(true);

                mostrarMensaje(
                        "Error de conexión al registrar ubicación."
                );
            }
        });
    }

    // =====================================
    // EDITAR UBICACIÓN
    // =====================================
    private void mostrarDialogoEditar(Ubicacion ubicacion) {

        LinearLayout formulario = new LinearLayout(this);
        formulario.setOrientation(LinearLayout.VERTICAL);

        formulario.setPadding(
                dp(20), dp(8), dp(20), 0
        );

        EditText inputCiudad = new EditText(this);
        inputCiudad.setHint("Ciudad");
        inputCiudad.setSingleLine(true);
        inputCiudad.setText(ubicacion.getCiudad());

        EditText inputProvincia = new EditText(this);
        inputProvincia.setHint("Provincia");
        inputProvincia.setSingleLine(true);
        inputProvincia.setText(ubicacion.getProvincia());

        formulario.addView(inputCiudad);
        formulario.addView(inputProvincia);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Modificar ubicación")
                .setView(formulario)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialogo.setOnShowListener(d -> {

            Button botonGuardar = dialogo.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            botonGuardar.setOnClickListener(v -> {

                String ciudad = inputCiudad
                        .getText().toString().trim();

                String provincia = inputProvincia
                        .getText().toString().trim();

                if (!validarDatos(
                        ciudad,
                        provincia,
                        inputCiudad,
                        inputProvincia
                )) {
                    return;
                }

                if (ciudad.equals(ubicacion.getCiudad())
                        && provincia.equals(ubicacion.getProvincia())) {

                    dialogo.dismiss();
                    return;
                }

                botonGuardar.setEnabled(false);

                actualizarUbicacion(
                        ubicacion.getId(),
                        ciudad,
                        provincia,
                        dialogo
                );
            });
        });

        dialogo.show();
    }

    // =====================================
    // ACTUALIZAR UBICACIÓN EN DJANGO
    // =====================================
    private void actualizarUbicacion(
            int id,
            String ciudad,
            String provincia,
            AlertDialog dialogo) {

        Map<String, String> datos = new HashMap<>();
        datos.put("ciudad", ciudad);
        datos.put("provincia", provincia);

        apiService.actualizarUbicacion(
                id,
                "Bearer " + token,
                datos
        ).enqueue(new Callback<Ubicacion>() {

            @Override
            public void onResponse(
                    Call<Ubicacion> call,
                    Response<Ubicacion> response) {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (response.isSuccessful()) {

                    dialogo.dismiss();

                    mostrarMensaje(
                            "Ubicación modificada correctamente."
                    );

                    cargarUbicaciones();

                } else {

                    dialogo.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setEnabled(true);

                    mostrarError(response.code(), "modificar");
                }
            }

            @Override
            public void onFailure(
                    Call<Ubicacion> call,
                    Throwable t) {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                dialogo.getButton(
                        AlertDialog.BUTTON_POSITIVE
                ).setEnabled(true);

                mostrarMensaje(
                        "Error de conexión al modificar ubicación."
                );
            }
        });
    }

    // =====================================
    // CONFIRMAR ELIMINACIÓN
    // =====================================
    private void confirmarEliminacion(Ubicacion ubicacion) {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar ubicación")
                .setMessage(
                        "¿Seguro que querés eliminar "
                                + ubicacion.getCiudad()
                                + ", "
                                + ubicacion.getProvincia()
                                + "?"
                )
                .setPositiveButton(
                        "Eliminar",
                        (dialog, which) ->
                                eliminarUbicacion(ubicacion.getId())
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =====================================
    // ELIMINAR UBICACIÓN
    // =====================================
    private void eliminarUbicacion(int id) {

        apiService.eliminarUbicacion(
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
                            "Ubicación eliminada correctamente."
                    );

                    cargarUbicaciones();

                } else if (response.code() == 409
                        || response.code() == 500) {

                    mostrarMensaje(
                            "No se pudo eliminar la ubicación. "
                                    + "Puede estar asociada a usuarios."
                    );

                } else {

                    mostrarError(response.code(), "eliminar");
                }
            }

            @Override
            public void onFailure(
                    Call<Void> call,
                    Throwable t) {

                if (!isFinishing() && !isDestroyed()) {
                    mostrarMensaje(
                            "Error de conexión al eliminar ubicación."
                    );
                }
            }
        });
    }

    // =====================================
    // VALIDAR DATOS
    // =====================================
    private boolean validarDatos(
            String ciudad,
            String provincia,
            EditText campoCiudad,
            EditText campoProvincia) {

        if (ciudad.length() < 2) {

            campoCiudad.setError(
                    "Ingresá una ciudad válida."
            );

            campoCiudad.requestFocus();
            return false;
        }

        if (provincia.length() < 2) {

            campoProvincia.setError(
                    "Ingresá una provincia válida."
            );

            campoProvincia.requestFocus();
            return false;
        }

        return true;
    }

    // =====================================
    // MOSTRAR ERRORES
    // =====================================
    private void mostrarError(int codigo, String accion) {

        if (codigo == 400) {

            mostrarMensaje(
                    "Datos inválidos. Revisá la ciudad y provincia."
            );

        } else if (codigo == 401) {

            mostrarMensaje(
                    "Tu sesión expiró. Iniciá sesión nuevamente."
            );

        } else if (codigo == 403) {

            mostrarMensaje(
                    "No tenés permisos para " + accion + " ubicaciones."
            );

        } else if (codigo == 404) {

            mostrarMensaje(
                    "La ubicación no fue encontrada."
            );

        } else {

            mostrarMensaje(
                    "Error al " + accion + " ubicación. Código: "
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

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        navSalirAdmin.setOnClickListener(v -> cerrarSesion());
    }

    // =====================================
    // CERRAR SESIÓN
    // =====================================
    private void cerrarSesion() {

        SharedPreferences sesion = getSharedPreferences(
                "ServiMatchSesion",
                MODE_PRIVATE
        );

        sesion.edit().clear().apply();

        SharedPreferences preferencias = getSharedPreferences(
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
