package pt.unl.fct.di.aldeia.apdc2021.ui.register;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.LoginDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.LoginRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.RegisterDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.RegisterRepository;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginViewModel;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {

    private Executor executor;

    public RegisterViewModelFactory (Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(RegisterRepository.getInstance(new RegisterDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}