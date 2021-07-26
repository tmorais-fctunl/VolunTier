package pt.unl.fct.di.aldeia.apdc2021.ui.recoverPassword;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.RecoverPwDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.RecoverPwRepository;


public class RecoverPwViewModelFactory implements ViewModelProvider.Factory {

    private final Executor executor;

    public RecoverPwViewModelFactory (Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecoverPwViewModel.class)) {
            return (T) new RecoverPwViewModel(RecoverPwRepository.getInstance(new RecoverPwDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
