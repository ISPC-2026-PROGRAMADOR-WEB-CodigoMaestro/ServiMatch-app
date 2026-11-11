
package com.ispc.servimatch;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.ispc.servimatch.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText edtNombre, edtEmail, edtDni;
    private Spinner spinnerOficio, spinnerUbicacion;
    private Button btnGuardar, btnCancelar;

    private ApiService apiService;
    private int idUsuario = -1;
    private String token;

    private final List<Integer> idsOficios = new ArrayList<>();
    private final List<Integer> idsUbicaciones = new ArrayList<>();

    private Integer oficioActual;
    private Integer ubicacionActual;

    private boolean usuarioCargado = false;
    private boolean oficiosCargados = false;
    private boolean ubicacionesCargadas = false;
    private boolean guardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        edtNombre = findViewById(R.id.edtPerfilNombre);
        edtEmail = findViewById(R.id.edtPerfilEmail);
        edtDni = findViewById(R.id.edtPerfilDni);

        spinnerOficio = findViewById(R.id.spinnerPerfilOficio);
        spinnerUbicacion = findViewById(R.id.spinnerPerfilUbicacion);

        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnCancelar = findViewById(R.id.btnCancelarPerfil);

        btnGuardar.setEnabled(false);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        SharedPreferences preferencias = getSharedPreferences(
                "ServiMatchSesion", MODE_PRIVATE
        );

        idUsuario = preferencias.getInt("id_usuario", -1);
        token = preferencias.getString("access_token", null);
        int idRol = preferencias.getInt("id_rol", -1);

        if (idRol != 11) {
            mostrarMensaje("Acceso permitido solo a usuarios estándar.");
            finish();
            return;
        }

        if (idUsuario <= 0 || token == null || token.isEmpty()) {
            mostrarMensaje("No se pudo identificar el usuario o la sesión.");
            finish();
            return;
        }

        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarCambios());

        cargarUsuario();
        cargarOficios();
        cargarUbicaciones();
    }

    // CARGAR USUARIO
    private void cargarUsuario() {
        apiService.obtenerUsuario(idUsuario, "Bearer " + token)
                .enqueue(new Callback<Usuario>() {

                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (isFinishing() || isDestroyed()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            Usuario usuario = response.body();

                            edtNombre.setText(usuario.getNombre());
                            edtEmail.setText(usuario.getEmail());
                            edtDni.setText(usuario.getDni());

                            oficioActual = usuario.getOficio();
                            ubicacionActual = usuario.getUbicacion();

                            usuarioCargado = true;
                            seleccionarOficio();
                            seleccionarUbicacion();
                            verificarCarga();

                        } else {
                            mostrarMensaje("Error al cargar perfil. Código: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        if (!isFinishing()) {
                            mostrarMensaje("Error de conexión al cargar perfil.");
                        }
                    }
                });
    }

    // CARGAR OFICIOS
    private void cargarOficios() {
        apiService.obtenerOficios().enqueue(new Callback<List<Oficio>>() {

            @Override
            public void onResponse(Call<List<Oficio>> call, Response<List<Oficio>> response) {
                if (isFinishing() || isDestroyed()) return;

                if (!response.isSuccessful() || response.body() == null) {
                    mostrarMensaje("No se pudieron cargar los oficios.");
                    return;
                }

                List<String> nombres = new ArrayList<>();
                nombres.add("Sin oficio");
                idsOficios.clear();
                idsOficios.add(null);

                for (Oficio oficio : response.body()) {
                    nombres.add(oficio.getNombre_oficio());
                    idsOficios.add(oficio.getId());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        EditarPerfilActivity.this,
                        R.layout.spinner_item, nombres
                );

                adapter.setDropDownViewResource(
                        R.layout.spinner_item
                );

                spinnerOficio.setAdapter(adapter);

                oficiosCargados = true;
                seleccionarOficio();
                verificarCarga();
            }

            @Override
            public void onFailure(Call<List<Oficio>> call, Throwable t) {
                if (!isFinishing()) {
                    mostrarMensaje("Error de conexión al cargar oficios.");
                }
            }
        });
    }

    // CARGAR UBICACIONES
    private void cargarUbicaciones() {
        apiService.obtenerUbicaciones().enqueue(new Callback<List<Ubicacion>>() {

            @Override
            public void onResponse(Call<List<Ubicacion>> call, Response<List<Ubicacion>> response) {
                if (isFinishing() || isDestroyed()) return;

                if (!response.isSuccessful() || response.body() == null) {
                    mostrarMensaje("No se pudieron cargar las ubicaciones.");
                    return;
                }

                List<String> nombres = new ArrayList<>();
                idsUbicaciones.clear();

                for (Ubicacion ubicacion : response.body()) {
                    nombres.add(ubicacion.getCiudad() + ", " + ubicacion.getProvincia());
                    idsUbicaciones.add(ubicacion.getId());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        EditarPerfilActivity.this,
                        R.layout.spinner_item, nombres
                );

                adapter.setDropDownViewResource(
                        R.layout.spinner_item
                );

                spinnerUbicacion.setAdapter(adapter);

                ubicacionesCargadas = true;
                seleccionarUbicacion();
                verificarCarga();
            }

            @Override
            public void onFailure(Call<List<Ubicacion>> call, Throwable t) {
                if (!isFinishing()) {
                    mostrarMensaje("Error de conexión al cargar ubicaciones.");
                }
            }
        });
    }

    // SELECCIONAR OFICIO
    private void seleccionarOficio() {
        if (!usuarioCargado || !oficiosCargados) return;

        int posicion = idsOficios.indexOf(oficioActual);

        if (posicion >= 0) {
            spinnerOficio.setSelection(posicion);
        } else {
            mostrarMensaje("El oficio actual no está disponible.");
            btnGuardar.setEnabled(false);
        }
    }

    // SELECCIONAR UBICACIÓN
    private void seleccionarUbicacion() {
        if (!usuarioCargado || !ubicacionesCargadas) return;

        int posicion = idsUbicaciones.indexOf(ubicacionActual);

        if (posicion >= 0) {
            spinnerUbicacion.setSelection(posicion);
        } else {
            mostrarMensaje("La ubicación actual no está disponible.");
            btnGuardar.setEnabled(false);
        }
    }

    // VERIFICAR CARGA
    private void verificarCarga() {
        if (!usuarioCargado || !oficiosCargados || !ubicacionesCargadas) {
            return;
        }

        boolean oficioValido = idsOficios.contains(oficioActual);
        boolean ubicacionValida = idsUbicaciones.contains(ubicacionActual);

        btnGuardar.setEnabled(oficioValido && ubicacionValida && !guardando);
    }

    // GUARDAR CAMBIOS
    private void guardarCambios() {
        if (guardando || !btnGuardar.isEnabled()) return;

        String nombre = edtNombre.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String dni = edtDni.getText().toString().trim();

        if (nombre.isEmpty()) {
            edtNombre.setError("Ingresá el nombre.");
            return;
        }

        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            Toast.makeText(this,
                    "El nombre solo puede contener letras y espacios.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this,
                    "Ingresá un correo válido.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dni.matches("\\d{7,8}")) {
            Toast.makeText(this,
                    "El DNI debe tener 7 u 8 dígitos.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int posicionOficio = spinnerOficio.getSelectedItemPosition();
        int posicionUbicacion = spinnerUbicacion.getSelectedItemPosition();

        if (posicionOficio < 0 || posicionOficio >= idsOficios.size()
                || posicionUbicacion < 0
                || posicionUbicacion >= idsUbicaciones.size()) {

            mostrarMensaje("Seleccioná un oficio y una ubicación válidos.");
            return;
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("email", email);
        datos.put("dni", dni);
        datos.put("oficio", idsOficios.get(posicionOficio));
        datos.put("ubicacion", idsUbicaciones.get(posicionUbicacion));

        guardando = true;
        btnGuardar.setEnabled(false);

        apiService.actualizarUsuario(idUsuario, "Bearer " + token, datos)
                .enqueue(new Callback<Usuario>() {

                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (isFinishing() || isDestroyed()) return;

                        guardando = false;

                        if (response.isSuccessful()) {
                            mostrarMensaje("Perfil actualizado correctamente.");
                            finish();
                        } else {
                            verificarCarga();

                            if (response.code() == 400) {
                                mostrarMensaje("Datos inválidos o duplicados. Revisá el formulario.");
                            } else if (response.code() == 401) {
                                mostrarMensaje("La sesión expiró.");
                            } else if (response.code() == 403) {
                                mostrarMensaje("No tenés permisos para editar este perfil.");
                            } else {
                                mostrarMensaje("No se pudieron guardar los cambios. Código: " + response.code());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        if (isFinishing() || isDestroyed()) return;

                        guardando = false;
                        verificarCarga();
                        mostrarMensaje("Error de conexión al guardar los cambios.");
                    }
                });
    }

    // MOSTRAR MENSAJES
    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
