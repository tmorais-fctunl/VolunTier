package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetParticipants;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteParticipantsDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.RateRouteDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.RateRouteRepository;
import pt.unl.fct.di.aldeia.apdc2021.ui.route.RateRouteViewModel;

public class RateRouteViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public RateRouteViewModelFactory(Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RateRouteViewModel.class)) {
            return (T) new RateRouteViewModel(RateRouteRepository.getInstance(new RateRouteDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}