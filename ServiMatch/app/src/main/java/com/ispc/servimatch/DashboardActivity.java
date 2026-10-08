package com.ispc.servimatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtBienvenida;

    // Cards de oficios
    private LinearLayout cardElectricista;
    private LinearLayout cardPlomero;
    private LinearLayout cardGasista;
    private LinearLayout cardCarpintero;
    private LinearLayout cardPintor;
    private LinearLayout cardJardinero;
    private LinearLayout cardCerrajero;
    private LinearLayout cardMecanico;

    // Barra de navegación
    private LinearLayout navInicio;
    private LinearLayout navContacto;
    private LinearLayout navPerfil;

    // Elementos visuales de Inicio
    private TextView iconInicio;
    private TextView textoInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // =========================================
        // VINCULAR ELEMENTOS DEL DASHBOARD
        // =========================================

        txtBienvenida =
                findViewById(R.id.txtBienvenida);

        // Cards
        cardElectricista =
                findViewById(R.id.cardElectricista);

        cardPlomero =
                findViewById(R.id.cardPlomero);

        cardGasista =
                findViewById(R.id.cardGasista);

        cardCarpintero =
                findViewById(R.id.cardCarpintero);

        cardPintor =
                findViewById(R.id.cardPintor);

        cardJardinero =
                findViewById(R.id.cardJardinero);

        cardCerrajero =
                findViewById(R.id.cardCerrajero);

        cardMecanico =
                findViewById(R.id.cardMecanico);

        // =========================================
        // RECIBIR DATOS DEL USUARIO DESDE LOGIN
        // =========================================

        String usuario =
                getIntent().getStringExtra("usuario");

        int idUsuario =
                getIntent().getIntExtra(
                        "id_usuario",
                        -1
                );

        int idRol =
                getIntent().getIntExtra(
                        "id_rol",
                        -1
                );

        String rol =
                getIntent().getStringExtra("rol");

        // =========================================
        // MOSTRAR BIENVENIDA
        // =========================================

        if (usuario != null
                && !usuario.isEmpty()) {

            txtBienvenida.setText(
                    "¡Hola, " + usuario + "!"
            );

        } else {

            txtBienvenida.setText(
                    "¡Hola!"
            );
        }

        // =========================================
        // EVENTOS DE LAS CARDS
        // =========================================

        cardElectricista.setOnClickListener(v -> {

            abrirProfesionales(
                    "Electricista"
            );

        });

        cardPlomero.setOnClickListener(v -> {

            abrirProfesionales(
                    "Plomero"
            );

        });

        cardGasista.setOnClickListener(v -> {

            abrirProfesionales(
                    "Gasista"
            );

        });

        cardCarpintero.setOnClickListener(v -> {

            abrirProfesionales(
                    "Carpintero"
            );

        });

        cardPintor.setOnClickListener(v -> {

            abrirProfesionales(
                    "Pintor"
            );

        });

        cardJardinero.setOnClickListener(v -> {

            abrirProfesionales(
                    "Jardinero"
            );

        });

        cardCerrajero.setOnClickListener(v -> {

            abrirProfesionales(
                    "Cerrajero"
            );

        });

        cardMecanico.setOnClickListener(v -> {

            abrirProfesionales(
                    "Mecánico"
            );

        });

        // =========================================
        // BARRA DE NAVEGACIÓN
        // =========================================

        navInicio =
                findViewById(R.id.navInicio);

        navContacto =
                findViewById(R.id.navContacto);

        navPerfil =
                findViewById(R.id.navPerfil);

        iconInicio =
                findViewById(R.id.iconInicio);

        textoInicio =
                findViewById(R.id.textoInicio);

        // =========================================
        // MARCAR INICIO COMO SELECCIONADO
        // =========================================

        iconInicio.setTextColor(
                getResources().getColor(
                        R.color.color_primario
                )
        );

        textoInicio.setTextColor(
                getResources().getColor(
                        R.color.color_primario
                )
        );

        textoInicio.setTypeface(
                null,
                Typeface.BOLD
        );

        // =========================================
        // NAVEGACIÓN - INICIO
        // =========================================

        navInicio.setOnClickListener(v -> {

            Toast.makeText(
                    DashboardActivity.this,
                    "Ya estás en Inicio",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // =========================================
        // NAVEGACIÓN - CONTACTO
        // =========================================

        navContacto.setOnClickListener(v -> {

            Toast.makeText(
                    DashboardActivity.this,
                    "Próximamente: Contacto",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // =========================================
        // NAVEGACIÓN - PERFIL
        // =========================================

        navPerfil.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            DashboardActivity.this,
                            PerfilActivity.class
                    );

            // Enviamos los datos del usuario
            // que inició sesión.

            if (usuario != null) {

                intent.putExtra(
                        "usuario",
                        usuario
                );
            }

            intent.putExtra(
                    "id_usuario",
                    idUsuario
            );

            intent.putExtra(
                    "id_rol",
                    idRol
            );

            if (rol != null) {

                intent.putExtra(
                        "rol",
                        rol
                );
            }

            startActivity(intent);

        });
    }

    // =============================================
    // ABRIR LISTADO DE PROFESIONALES
    // =============================================

    private void abrirProfesionales(
            String oficio) {

        Intent intent =
                new Intent(
                        DashboardActivity.this,
                        ProfesionalesActivity.class
                );

        // Enviamos el oficio seleccionado
        intent.putExtra(
                "oficio",
                oficio
        );

        startActivity(intent);
    }
}