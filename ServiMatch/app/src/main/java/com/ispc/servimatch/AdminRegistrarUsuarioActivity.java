
package com.ispc.servimatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Ubicacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRegistrarUsuarioActivity extends AppCompatActivity {

    private EditText edtNombre, edtEmail, edtDni, edtContrasena;
    private Spinner spinnerOficio, spinnerUbicacion;
    private Button btnGuardar, btnCancelar;

    private ApiService apiService;
    private String token;

    private final List<Integer> idsOficios = new ArrayList<>();
    private final List<Integer> idsUbicaciones = new ArrayList<>();

    private boolean oficiosCargados = false;
    private boolean ubicacionesCargadas = false;
    private boolean registrando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registrar_usuario);

        edtNombre = findViewById(R.id.edtAdminRegistroNombre);
        edtEmail = findViewById(R.id.edtAdminRegistroEmail);
        edtDni = findViewById(R.id.edtAdminRegistroDni);
        edtContrasena = findViewById(R.id.edtAdminRegistroContrasena);

        spinnerOficio = findViewById(R.id.spinnerAdminRegistroOficio);
        spinnerUbicacion = findViewById(R.id.spinnerAdminRegistroUbicacion);

        btnGuardar = findViewById(R.id.btnAdminGuardarUsuario);
        btnCancelar = findViewById(R.id.btnAdminCancelarRegistro);

        btnGuardar.setEnabled(false);

        SharedPreferences preferencias = getSharedPreferences(
                "ServiMatchSesion",
                MODE_PRIVATE
        );

        token = preferencias.getString("access_token", null);
        int idRol = preferencias.getInt("id_rol", -1);

        if (idRol != 10 || token == null || token.isEmpty()) {
            mostrarMensaje("Acceso permitido solo a administradores.");
            finish();
            return;
        }

        apiService = RetrofitClient
                .getClient(this)
                .create(ApiService.class);

        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> registrarUsuario());

        cargarOficios();
        cargarUbicaciones();
    }

    private void cargarOficios() {

        apiService.obtenerOficios().enqueue(
                new Callback<List<Oficio>>() {

                    @Override
                    public void onResponse(
                            Call<List<Oficio>> call,
                            Response<List<Oficio>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            mostrarMensaje("No se pudieron cargar los oficios.");
                            return;
                        }

                        List<String> nombres = new ArrayList<>();

                        nombres.add("Sin oficio");
                        idsOficios.add(null);

                        for (Oficio oficio : response.body()) {
                            nombres.add(oficio.getNombre_oficio());
                            idsOficios.add(oficio.getId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                AdminRegistrarUsuarioActivity.this,
                                android.R.layout.simple_spinner_item,
                                nombres
                        );

                        adapter.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item
                        );

                        spinnerOficio.setAdapter(adapter);

                        oficiosCargados = true;
                        verificarCarga();
                    }

                    @Override
                    public void onFailure(
                            Call<List<Oficio>> call,
                            Throwable t) {

                        mostrarMensaje("Error de conexión al cargar oficios.");
                    }
                }
        );
    }

    private void cargarUbicaciones() {

        apiService.obtenerUbicaciones().enqueue(
                new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            mostrarMensaje("No se pudieron cargar las ubicaciones.");
                            return;
                        }

                        List<String> nombres = new ArrayList<>();

                        for (Ubicacion ubicacion : response.body()) {
                            nombres.add(
                                    ubicacion.getCiudad() + ", "
                                            + ubicacion.getProvincia()
                            );

                            idsUbicaciones.add(ubicacion.getId());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                AdminRegistrarUsuarioActivity.this,
                                android.R.layout.simple_spinner_item,
                                nombres
                        );

                        adapter.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item
                        );

                        spinnerUbicacion.setAdapter(adapter);

                        ubicacionesCargadas = !idsUbicaciones.isEmpty();

                        if (!ubicacionesCargadas) {
                            mostrarMensaje("No hay ubicaciones disponibles.");
                        }

                        verificarCarga();
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        mostrarMensaje("Error de conexión al cargar ubicaciones.");
                    }
                }
        );
    }

    private void verificarCarga() {
        btnGuardar.setEnabled(
                oficiosCargados && ubicacionesCargadas && !registrando
        );
    }

    private void registrarUsuario() {

        if (registrando || !oficiosCargados || !ubicacionesCargadas) {
            return;
        }

        String nombre = edtNombre.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String dni = edtDni.getText().toString().trim();
        String contrasena = edtContrasena.getText().toString();

        if (nombre.isEmpty()) {
            edtNombre.setError("Ingresá el nombre.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Ingresá un correo válido.");
            return;
        }

        if (!dni.matches("\\d{7,8}")) {
            edtDni.setError("El DNI debe tener 7 u 8 dígitos.");
            return;
        }

        if (contrasena.length() < 8) {
            edtContrasena.setError(
                    "La contraseña debe tener al menos 8 caracteres."
            );
            return;
        }

        int posicionOficio = spinnerOficio.getSelectedItemPosition();
        int posicionUbicacion = spinnerUbicacion.getSelectedItemPosition();

        if (posicionOficio < 0 || posicionOficio >= idsOficios.size()
                || posicionUbicacion < 0
                || posicionUbicacion >= idsUbicaciones.size()) {

            mostrarMensaje("Seleccioná una ubicación válida.");
            return;
        }

        Map<String, Object> datos = new HashMap<>();

        datos.put("nombre", nombre);
        datos.put("email", email);
        datos.put("dni", dni);
        datos.put("contrasena", contrasena);
        datos.put("oficio", idsOficios.get(posicionOficio));
        datos.put("ubicacion", idsUbicaciones.get(posicionUbicacion));

        registrando = true;
        verificarCarga();

        apiService.registrarUsuarioAdmin(
                "Bearer " + token,
                datos
        ).enqueue(new Callback<Map<String, Object>>() {

            @Override
            public void onResponse(
                    Call<Map<String, Object>> call,
                    Response<Map<String, Object>> response) {

                registrando = false;

                if (response.isSuccessful()) {

                    mostrarMensaje("Usuario registrado correctamente.");
                    setResult(RESULT_OK);
                    finish();

                } else {

                    verificarCarga();

                    if (response.code() == 400) {
                        mostrarMensaje(
                                "Datos inválidos o duplicados. Revisá el formulario."
                        );
                    } else if (response.code() == 401) {
                        mostrarMensaje("La sesión expiró.");
                    } else if (response.code() == 403) {
                        mostrarMensaje("No tenés permisos para registrar usuarios.");
                    } else {
                        mostrarMensaje(
                                "Error al registrar usuario. Código: "
                                        + response.code()
                        );
                    }
                }
            }

            @Override
            public void onFailure(
                    Call<Map<String, Object>> call,
                    Throwable t) {

                registrando = false;
                verificarCarga();

                mostrarMensaje("Error de conexión con el servidor.");
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
