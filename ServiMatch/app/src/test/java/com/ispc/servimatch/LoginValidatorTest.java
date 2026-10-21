
package com.ispc.servimatch;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginValidatorTest {

    @Test
    public void camposVacios_retornaFalse() {

        // Arrange
        String usuario = "";
        String password = "";

        // Act
        boolean resultado =
                LoginValidator.validarCampos(usuario, password);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void usuarioVacio_retornaFalse() {

        // Arrange
        String usuario = "";
        String password = "123456";

        // Act
        boolean resultado =
                LoginValidator.validarCampos(usuario, password);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void passwordVacia_retornaFalse() {

        // Arrange
        String usuario = "usuario@gmail.com";
        String password = "";

        // Act
        boolean resultado =
                LoginValidator.validarCampos(usuario, password);

        // Assert
        assertFalse(resultado);
    }

    @Test
    public void camposCompletos_retornaTrue() {

        // Arrange
        String usuario = "usuario@gmail.com";
        String password = "123456";

        // Act
        boolean resultado =
                LoginValidator.validarCampos(usuario, password);

        // Assert
        assertTrue(resultado);
    }
}
