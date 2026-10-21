
package com.ispc.servimatch;

public class LoginValidator {

    public static boolean validarCampos(
            String usuario,
            String password) {

        if (usuario == null || password == null) {
            return false;
        }

        return !usuario.trim().isEmpty()
                && !password.isEmpty();
    }
}

