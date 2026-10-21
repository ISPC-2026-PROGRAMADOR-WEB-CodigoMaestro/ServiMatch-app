
package com.ispc.servimatch;

public class RegistroValidator {

    public static boolean validarEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
    }

    // Validación del DNI
    public static boolean validarDNI(String dni) {

        if (dni == null || dni.trim().isEmpty()) {
            return false;
        }

        return dni.matches("\\d{7,8}");
    }

    // Validación del nombre completo
    public static boolean validarNombre(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        return nombre.matches(
                "[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+"
        );
    }

}

