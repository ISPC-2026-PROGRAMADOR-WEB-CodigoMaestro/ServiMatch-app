package com.ispc.servimatch;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import com.ispc.servimatch.api.ApiService;
import com.ispc.servimatch.api.RetrofitClient;
import com.ispc.servimatch.model.Ubicacion;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    // Declaramos los componentes
    private EditText etNombreCompleto;
    private EditText etEmail;
    private EditText etDNI;
    private EditText etContrasena;
    private Spinner spUbicacion;
    private Spinner spOficio;
    private Button btnRegistrar;

    // Lista donde guardamos las ubicaciones obtenidas desde Django
    private List<Ubicacion> listaUbicaciones = new ArrayList<>();

    // Lista donde guardamos los oficios obtenidos desde Django
    private List<Oficio> listaOficios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Enlazamos los componentes Java con su ID XML
        etNombreCompleto = findViewById(R.id.etNombreCompleto);
        etEmail = findViewById(R.id.etEmail);
        etDNI = findViewById(R.id.etDNI);
        etContrasena = findViewById(R.id.etContrasena);
        spUbicacion = findViewById(R.id.spUbicacion);
        spOficio = findViewById(R.id.spOficio);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Cargamos las ubicaciones desde la API de Django
        cargarUbicaciones();

        // Cargamos los oficios desde la API de Django
        cargarOficios();

        // Capturamos el clic del boton
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = etNombreCompleto.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String dni = etDNI.getText().toString().trim();
                String contrasena = etContrasena.getText().toString().trim();

                String ubicacion = "";

                if (spUbicacion.getSelectedItemPosition() != 0) {
                    ubicacion = spUbicacion.getSelectedItem().toString();
                }

                String oficio = "";

                if (spOficio.getSelectedItemPosition() != 0) {
                    oficio = spOficio.getSelectedItem().toString();
                }

                // Validamos que los campos obligatorios no esten vacios
                if (nombre.isEmpty() || email.isEmpty() || dni.isEmpty()
                        || contrasena.isEmpty()) {

                    Toast.makeText(RegistroActivity.this,
                            "Complete todos los campos obligatorios",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                // Validamos el campo Nombre Completo
                if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+")) {

                    Toast.makeText(RegistroActivity.this,
                            "El nombre solo puede contener letras",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                // Validamos el campo Correo Electronico
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    Toast.makeText(RegistroActivity.this,
                            "Ingrese un correo electronico valido",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                // Validamos el campo DNI
                if (!dni.matches("\\d{8}")) {

                    Toast.makeText(RegistroActivity.this,
                            "El DNI debe tener 8 digitos",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                // Validamos el campo Contrasena
                if (contrasena.length() < 6) {

                    Toast.makeText(RegistroActivity.this,
                            "La contraseña debe tener al menos 6 caracteres",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                // Validamos que se haya seleccionado una ubicacion
                if (spUbicacion.getSelectedItemPosition() == 0) {

                    Toast.makeText(RegistroActivity.this,
                            "Seleccione una ubicación",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                int posicionUbicacion = spUbicacion.getSelectedItemPosition() - 1;
                Integer ubicacionId = listaUbicaciones.get(posicionUbicacion).getId();

                Integer oficioId = null;

                if (spOficio.getSelectedItemPosition() != 0) {
                    int posicionOficio = spOficio.getSelectedItemPosition() - 1;
                    oficioId = listaOficios.get(posicionOficio).getId();
                }

                Integer rolId = 11;

                Usuario usuario = new Usuario(
                        nombre,
                        email,
                        contrasena,
                        dni,
                        rolId,
                        oficioId,
                        ubicacionId
                );

                ApiService apiService =
                        RetrofitClient.getClient().create(ApiService.class);

                apiService.registrarUsuario(usuario).enqueue(new Callback<Usuario>() {

                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(RegistroActivity.this,
                                    "Registro exitoso",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(
                                    RegistroActivity.this,
                                    LoginActivity.class
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(RegistroActivity.this,
                                    "Error al registrar el usuario",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {

                        Toast.makeText(RegistroActivity.this,
                                "No se pudo conectar con el servidor",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Cargamos las ubicaciones desde la API de Django
    private void cargarUbicaciones() {

        // Creamos la conexion con la API
        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        // Solicitamos las ubicaciones a Django
        apiService.obtenerUbicaciones().enqueue(new Callback<List<Ubicacion>>() {

            @Override
            public void onResponse(Call<List<Ubicacion>> call,
                                   Response<List<Ubicacion>> response) {

                // Verificamos que la respuesta sea correcta
                if (response.isSuccessful() && response.body() != null) {

                    listaUbicaciones = response.body();

                    // Creamos la lista que se mostrara en el Spinner
                    List<String> ubicaciones = new ArrayList<>();
                    ubicaciones.add("Seleccione una ubicación");

                    // Agregamos las ciudades recibidas desde Django
                    for (Ubicacion ubicacion : listaUbicaciones) {
                        ubicaciones.add(ubicacion.getCiudad());
                    }

                    // Cargamos las ubicaciones en el Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            RegistroActivity.this,
                            R.layout.spinner_item,
                            ubicaciones
                    );

                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    spUbicacion.setAdapter(adapter);

                } else {

                    // Mostramos un mensaje si Django responde con un error
                    Toast.makeText(RegistroActivity.this,
                            "Error al cargar las ubicaciones",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ubicacion>> call, Throwable t) {

                // Mostramos un mensaje si no se puede conectar con Django
                Toast.makeText(RegistroActivity.this,
                        "No se pudo conectar con el servidor",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cargamos los oficios desde la API de Django
    private void cargarOficios() {

        // Creamos la conexion con la API
        ApiService apiService =
                RetrofitClient.getClient().create(ApiService.class);

        // Solicitamos los oficios a Django
        apiService.obtenerOficios().enqueue(new Callback<List<Oficio>>() {

            @Override
            public void onResponse(Call<List<Oficio>> call,
                                   Response<List<Oficio>> response) {

                // Verificamos que la respuesta sea correcta
                if (response.isSuccessful() && response.body() != null) {

                    listaOficios = response.body();

                    // Creamos la lista que se mostrara en el Spinner
                    List<String> oficios = new ArrayList<>();
                    oficios.add("Seleccione un oficio (opcional)");

                    // Agregamos los oficios recibidos desde Django
                    for (Oficio oficio : listaOficios) {
                        oficios.add(oficio.getNombre_oficio());
                    }

                    // Cargamos los oficios en el Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            RegistroActivity.this,
                            R.layout.spinner_item,
                            oficios
                    );

                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    spOficio.setAdapter(adapter);

                } else {

                    // Mostramos un mensaje si Django responde con un error
                    Toast.makeText(RegistroActivity.this,
                            "Error al cargar los oficios",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Oficio>> call, Throwable t) {

                // Mostramos un mensaje si no se puede conectar con Django
                Toast.makeText(RegistroActivity.this,
                        "No se pudo conectar con el servidor",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}