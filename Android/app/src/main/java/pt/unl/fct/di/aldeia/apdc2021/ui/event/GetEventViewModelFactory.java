package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.ChangePasswordDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.ChangePasswordRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.UpdateEventDataSource;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginViewModel;

public class GetEventViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public GetEventViewModelFactory (Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GetEventViewModel.class)) {
            return (T) new GetEventViewModel(GetEventRepository.getInstance(new GetEventDataSource(), new UpdateEventDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
