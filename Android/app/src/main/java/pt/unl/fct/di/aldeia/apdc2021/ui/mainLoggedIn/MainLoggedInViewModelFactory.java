package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.AddEventDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.ChangeProfileDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.LogoutDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.MainLoggedInRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.RemoveAccDataSource;

public class MainLoggedInViewModelFactory implements ViewModelProvider.Factory {

    private final Executor executor;

    public MainLoggedInViewModelFactory(Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainLoggedInViewModel.class)) {
            return (T) new MainLoggedInViewModel(MainLoggedInRepository.getInstance(new ChangeProfileDataSource(), new LogoutDataSource(), new RemoveAccDataSource(), new AddEventDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
