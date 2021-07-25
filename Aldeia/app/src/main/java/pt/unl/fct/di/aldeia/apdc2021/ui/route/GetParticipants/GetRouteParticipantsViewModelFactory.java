package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetParticipants;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteParticipantsDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteParticipantsRepository;

public class GetRouteParticipantsViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public GetRouteParticipantsViewModelFactory(Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GetRouteParticipantsViewModel.class)) {
            return (T) new GetRouteParticipantsViewModel(GetRouteParticipantsRepository.getInstance(new GetRouteParticipantsDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}