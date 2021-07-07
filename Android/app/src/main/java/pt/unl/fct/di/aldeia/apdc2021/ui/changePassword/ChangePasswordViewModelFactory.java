package pt.unl.fct.di.aldeia.apdc2021.ui.changePassword;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.ChangePasswordDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.ChangePasswordRepository;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginViewModel;

public class ChangePasswordViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public ChangePasswordViewModelFactory (Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel.class)) {
            return (T) new ChangePasswordViewModel(ChangePasswordRepository.getInstance(new ChangePasswordDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
