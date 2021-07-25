package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetParticipants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteParticipantsData;

public class GetRouteParticipantsViewModel extends ViewModel {
    private final GetRouteParticipantsRepository repository;
    private final MutableLiveData<GetRouteParticipantsResult> routeParticipantsResult = new MutableLiveData<>();
    private final Executor executor;
    GetRouteParticipantsViewModel(GetRouteParticipantsRepository repository, Executor executor) {
        this.executor = executor;
        this.repository = repository;
    }

    LiveData<GetRouteParticipantsResult> getRouteParticipants() {
        return routeParticipantsResult;
    }

    public void getParticipants(GetRouteParticipantsData route) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<GetParticipantsReply> result = repository.getParticipants(route);
                if (result instanceof Result.Success) {
                    routeParticipantsResult.postValue(new GetRouteParticipantsResult(((Result.Success<GetParticipantsReply>) result).getData(), null));
                } else {
                    routeParticipantsResult.postValue(new GetRouteParticipantsResult(null, R.string.event_participants_failed));
                }
            }
        });
    }
}