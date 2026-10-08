package com.ispc.servimatch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class LoginScreen {

    // Localizadores de los elementos del Login
    private static final int CAMPO_USUARIO = R.id.etUsuario;
    private static final int CAMPO_PASSWORD = R.id.etPassword;
    private static final int BOTON_LOGIN = R.id.btnIngresar;

    // Ingresar correo electrónico
    public LoginScreen escribirUsuario(String usuario) {
        onView(withId(CAMPO_USUARIO))
                .perform(typeText(usuario), closeSoftKeyboard());

        return this;
    }

    // Ingresar contraseña
    public LoginScreen escribirPassword(String password) {
        onView(withId(CAMPO_PASSWORD))
                .perform(typeText(password), closeSoftKeyboard());

        return this;
    }

    // Presionar el botón INGRESAR
    public void pulsarLogin() {
        onView(withId(BOTON_LOGIN))
                .perform(click());
    }
}
