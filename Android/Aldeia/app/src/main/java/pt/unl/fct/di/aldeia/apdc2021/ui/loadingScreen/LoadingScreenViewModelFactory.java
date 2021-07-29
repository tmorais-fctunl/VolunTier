package pt.unl.fct.di.aldeia.apdc2021.ui.loadingScreen;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.LoadingScreenRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.LoginDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.LoginRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.LookUpDataSource;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginViewModel;

public class LoadingScreenViewModelFactory implements ViewModelProvider.Factory {

        private final Executor executor;

        public LoadingScreenViewModelFactory (Executor executor) {
            this.executor = executor;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LoadingScreenViewModel.class)) {
                return (T) new LoadingScreenViewModel(LoadingScreenRepository.getInstance(new LookUpDataSource()), executor);
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }

