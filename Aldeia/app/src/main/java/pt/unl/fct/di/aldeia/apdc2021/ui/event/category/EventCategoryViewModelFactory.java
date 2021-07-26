package pt.unl.fct.di.aldeia.apdc2021.ui.event.category;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.SearchEventCategoryDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.SearchEventCategoryRepository;

public class EventCategoryViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public EventCategoryViewModelFactory(Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EventCategoryViewModel.class)) {
            return (T) new EventCategoryViewModel(SearchEventCategoryRepository.getInstance(new SearchEventCategoryDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
