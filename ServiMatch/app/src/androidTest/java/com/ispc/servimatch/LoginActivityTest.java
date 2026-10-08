package com.ispc.servimatch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void loginExitoso_navegaADashboard() {

        // Arrange: preparar los datos de prueba
        String correo = "carlos@gmail.com";
        String contrasena = "Carlos1234";

        // Act: completar los campos e iniciar sesión
        new LoginScreen()
                .escribirUsuario(correo)
                .escribirPassword(contrasena)
                .pulsarLogin();

        // Assert: verificar que se muestra el Dashboard
        onView(withId(R.id.txtBienvenida))
                .check(matches(isDisplayed()));
    }
}