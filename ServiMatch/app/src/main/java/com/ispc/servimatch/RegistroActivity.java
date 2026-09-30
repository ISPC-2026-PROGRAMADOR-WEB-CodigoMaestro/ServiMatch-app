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

public class RegistroActivity extends AppCompatActivity {

    // Declaramos los componentes
    private EditText etNombreCompleto;
    private EditText etEmail;
    private EditText etDNI;
    private EditText etContrasena;
    private Spinner spUbicacion;
    private Spinner spOficio;
    private Button btnRegistrar;

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

        // Cargamos temporalmente las ubicaciones desde strings.xml
        ArrayAdapter<CharSequence> adapterUbicaciones =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.ubicaciones,
                        R.layout.spinner_item
                );

        adapterUbicaciones.setDropDownViewResource(
                R.layout.spinner_item
        );

        spUbicacion.setAdapter(adapterUbicaciones);

        // Cargamos temporalmente los oficios desde strings.xml
        ArrayAdapter<CharSequence> adapterOficios =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.oficios,
                        R.layout.spinner_item
                );

        adapterOficios.setDropDownViewResource(
                R.layout.spinner_item
        );

        spOficio.setAdapter(adapterOficios);

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

                // Si todas las validaciones son correctas
                Toast.makeText(RegistroActivity.this,
                        "Registro exitoso",
                        Toast.LENGTH_SHORT).show();

                //Redirigimos al Login
                Intent intent = new Intent(
                        RegistroActivity.this,
                        LoginActivity.class
                );

                startActivity(intent);

                 // Cerramos RegistroActivity
                finish();
            }
        });
    }
}