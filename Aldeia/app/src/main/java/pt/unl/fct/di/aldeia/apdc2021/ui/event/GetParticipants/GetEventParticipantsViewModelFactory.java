package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetParticipants;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsRepository;

public class GetEventParticipantsViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public GetEventParticipantsViewModelFactory(Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GetEventParticipantsViewModel.class)) {
            return (T) new GetEventParticipantsViewModel(GetEventParticipantsRepository.getInstance(new GetEventParticipantsDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
