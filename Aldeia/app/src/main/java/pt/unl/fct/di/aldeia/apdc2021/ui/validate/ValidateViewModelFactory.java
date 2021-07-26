package pt.unl.fct.di.aldeia.apdc2021.ui.validate;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.ValidateTokenDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.ValidateTokenRepository;


public class ValidateViewModelFactory implements ViewModelProvider.Factory {

    private final Executor executor;

    public ValidateViewModelFactory (Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ValidateViewModel.class)) {
            return (T) new ValidateViewModel(ValidateTokenRepository.getInstance(new ValidateTokenDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
