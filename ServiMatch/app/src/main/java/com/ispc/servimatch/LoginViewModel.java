
package com.ispc.servimatch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private final LoginRepository repository;

    private final MutableLiveData<Boolean> loginExitoso =
            new MutableLiveData<>();

    public LoginViewModel(LoginRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getLoginExitoso() {
        return loginExitoso;
    }

    public void iniciarSesion(String email, String password) {

        boolean resultado =
                repository.iniciarSesion(email, password);

        loginExitoso.setValue(resultado);
    }
}
