package pt.unl.fct.di.aldeia.apdc2021.ui.logout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.LogoutDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.LogoutRepository;

public class LogoutViewModelFactory implements ViewModelProvider.Factory {

    private Executor executor;

    public LogoutViewModelFactory(Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LogoutViewModel.class)) {
            return (T) new LogoutViewModel(LogoutRepository.getInstance(new LogoutDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
