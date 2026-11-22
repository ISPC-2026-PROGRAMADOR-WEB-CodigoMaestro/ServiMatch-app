package com.ispc.servimatch;

import org.junit.Test;
import static org.junit.Assert.*;

public class RegistroValidatorTest {

    // TC-REG-02: Campos obligatorios vacios

    @Test
    public void camposObligatoriosVacios() {

        // Arrange
        String nombre = "";
        String email = "";
        String dni = "";
        String contrasena = "";

        // Act
        boolean nombreValido = RegistroValidator.validarNombre(nombre);
        boolean emailValido = RegistroValidator.validarEmail(email);
        boolean dniValido = RegistroValidator.validarDNI(dni);
        boolean contrasenaValido = RegistroValidator.validarContrasena(contrasena);

        // Assert
        assertFalse(nombreValido);
        assertFalse(emailValido);
        assertFalse(dniValido);
        assertFalse(contrasenaValido);
    }

    // TC-REG-03: Nombre Invalido

    @Test
    public void nombreInvalido() {

        // Arrange
        String nombre = "Carlos1234*";

        // Act
        boolean resultado = RegistroValidator.validarNombre(nombre);

        // Assert
        assertFalse(resultado);
    }

    // TC-REG-04: Email Invalido

    @Test
    public void emailInvalido() {

        // Arrange
        String email = "carlosgmail.com";

        // Act
        boolean resultado = RegistroValidator.validarEmail(email);

        // Assert
        assertFalse(resultado);
    }

    // TC-REG-04: dniInvalido

    @Test
    public void dniInvalido() {

        // Arrange
        String dni = "25563";

        // Act
        boolean resultado = RegistroValidator.validarDNI(dni);

        // Assert
        assertFalse(resultado);
    }

    // TC-REG-06: contrasenaInvalido

    @Test
    public void contrasenaInvalido() {

        // Arrange
        String contrasena = "Carlo";

        // Act
        boolean resultado = RegistroValidator.validarContrasena(contrasena);

        // Assert
        assertFalse(resultado);
    }

    // TC-REG-07: Registro sin seleccionar ubicaccion

    @Test
    public void ubicacionInvalido() {

        // Arrange
        Integer ubicacionId = null;

        // Act
        boolean resultado = RegistroValidator.validarUbicacion(ubicacionId);

        // Assert
        assertFalse(resultado);
    }

    // TC-REG-08: Registro sin seleccionar oficio

    @Test
    public void oficioInvalido() {

        // Arrange
        Integer oficioId = null;

        // Act
        boolean resultado = RegistroValidator.validarOficio(oficioId);

        // Assert
        assertTrue(resultado);
    }
}

