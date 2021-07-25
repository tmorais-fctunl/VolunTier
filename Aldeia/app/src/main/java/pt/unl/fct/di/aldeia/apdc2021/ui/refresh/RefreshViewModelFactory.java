package pt.unl.fct.di.aldeia.apdc2021.ui.refresh;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.LoginDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.LoginRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.RefreshTokenDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.RefreshTokenRepository;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginViewModel;

public class RefreshViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public RefreshViewModelFactory (Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RefreshViewModel.class)) {
            return (T) new RefreshViewModel(RefreshTokenRepository.getInstance(new RefreshTokenDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
