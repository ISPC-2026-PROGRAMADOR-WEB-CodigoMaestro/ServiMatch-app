
package com.ispc.servimatch;

import org.junit.Test;
import static org.junit.Assert.*;

public class RegistroValidatorTest {

    @Test
    public void emailValido_retornaTrue() {
        // Arrange
        String email = "usuario@gmail.com";

        // Act
        boolean resultado = RegistroValidator.validarEmail(email);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void emailVacio_retornaFalse() {
        // Arrange
        String email = "";

        // Act
        boolean resultado = RegistroValidator.validarEmail(email);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void emailLimiteValido_retornaTrue() {
        // Arrange
        String email = "a@b.co";

        // Act
        boolean resultado = RegistroValidator.validarEmail(email);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void emailSinArroba_retornaFalse() {
        // Arrange
        String email = "usuariogmail.com";

        // Act
        boolean resultado = RegistroValidator.validarEmail(email);

        // Assert
        assertFalse(resultado);
    }


    @Test
    public void dniValido_retornaTrue() {

        // Arrange
        String dni = "12345678";

        // Act
        boolean resultado = RegistroValidator.validarDNI(dni);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void dniCorto_retornaFalse() {

        // Arrange
        String dni = "123456";

        // Act
        boolean resultado = RegistroValidator.validarDNI(dni);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void dniConLetras_retornaFalse() {

        // Arrange
        String dni = "1234ABCD";

        // Act
        boolean resultado = RegistroValidator.validarDNI(dni);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void nombreValido_retornaTrue() {

        // Arrange
        String nombre = "Juan Perez";

        // Act
        boolean resultado = RegistroValidator.validarNombre(nombre);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void nombreConNumeros_retornaFalse() {

        // Arrange
        String nombre = "Juan123";

        // Act
        boolean resultado = RegistroValidator.validarNombre(nombre);

        // Assert
        assertFalse(resultado);
    }
}

