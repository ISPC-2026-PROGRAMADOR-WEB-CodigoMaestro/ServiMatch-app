package com.ispc.servimatch;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class RegistroActivity extends AppCompatActivity {

    // Declaramos los componentes
    private EditText etNombreCompleto;
    private EditText etEmail;
    private EditText etDNI;
    private EditText etContrasena;
    private EditText etUbicacion;
    private EditText etOficio;
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
        etUbicacion = findViewById(R.id.etUbicacion);
        etOficio = findViewById(R.id.etOficio);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Capturamos el clic del boton
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombreCompleto.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String dni = etDNI.getText().toString().trim();
                String contrasena = etContrasena.getText().toString().trim();
                String ubicacion = etUbicacion.getText().toString().trim();
                String oficio = etOficio.getText().toString().trim();

                // Validamos que los campos obligatorios no esten vacios
                if (nombre.isEmpty() || email.isEmpty() || dni.isEmpty()
                        || contrasena.isEmpty() || ubicacion.isEmpty()) {

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
                if (dni.length() != 8) {

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

                // Si todas las validaciones son correctas
                Toast.makeText(RegistroActivity.this,
                        "Registro exitoso",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}