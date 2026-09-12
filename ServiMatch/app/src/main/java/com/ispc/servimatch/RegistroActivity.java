package com.ispc.servimatch;

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

                // Si los datos son correctos
                                Toast.makeText(RegistroActivity.this,
                                        "Registro exitoso",
                                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}