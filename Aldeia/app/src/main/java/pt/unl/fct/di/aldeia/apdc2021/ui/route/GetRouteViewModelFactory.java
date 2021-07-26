package pt.unl.fct.di.aldeia.apdc2021.ui.route;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.DeleteRouteDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.LeaveRouteDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.ParticipateEventDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.ParticipateRouteDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.RemoveEventDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.UpdateEventDataSource;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetEventViewModel;

public class GetRouteViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public GetRouteViewModelFactory (Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GetRouteViewModel.class)) {
            return (T) new GetRouteViewModel(GetRouteRepository.getInstance(new GetRouteDataSource(),new DeleteRouteDataSource(), new ParticipateRouteDataSource(), new LeaveRouteDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}

