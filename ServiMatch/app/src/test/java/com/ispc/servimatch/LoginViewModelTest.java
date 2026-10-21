
package com.ispc.servimatch;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule =
            new InstantTaskExecutorRule();

    private LoginRepository repository;
    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        repository = mock(LoginRepository.class);
        viewModel = new LoginViewModel(repository);
    }

    @Test
    public void loginCorrecto_retornaTrue() {

        // Arrange
        String email = "usuario@gmail.com";
        String password = "Password123!";

        when(repository.iniciarSesion(email, password))
                .thenReturn(true);

        // Act
        viewModel.iniciarSesion(email, password);

        // Assert
        assertEquals(
                Boolean.TRUE,
                viewModel.getLoginExitoso().getValue()
        );

        verify(repository, times(1))
                .iniciarSesion(email, password);
    }

    @Test
    public void loginIncorrecto_retornaFalse() {

        // Arrange
        String email = "usuario@gmail.com";
        String password = "incorrecta";

        when(repository.iniciarSesion(email, password))
                .thenReturn(false);

        // Act
        viewModel.iniciarSesion(email, password);

        // Assert
        assertEquals(
                Boolean.FALSE,
                viewModel.getLoginExitoso().getValue()
        );

        verify(repository, times(1))
                .iniciarSesion(email, password);
    }
}
