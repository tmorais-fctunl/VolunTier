package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetParticipants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;

public class GetEventParticipantsViewModel extends ViewModel {
    private final GetEventParticipantsRepository repository;
    private final MutableLiveData<GetEventParticipantsResult> eventParticipantsResult = new MutableLiveData<>();
    private final Executor executor;
    GetEventParticipantsViewModel(GetEventParticipantsRepository repository, Executor executor) {
        this.executor = executor;
        this.repository = repository;
    }

    LiveData<GetEventParticipantsResult> getEventParticipants() {
        return eventParticipantsResult;
    }

    public void getParticipants(GetEventParticipantsData event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<GetParticipantsReply> result = repository.getParticipants(event);
                if (result instanceof Result.Success) {
                    eventParticipantsResult.postValue(new GetEventParticipantsResult(((Result.Success<GetParticipantsReply>) result).getData(), null));
                } else {
                    eventParticipantsResult.postValue(new GetEventParticipantsResult(null, R.string.event_participants_failed));
                }
            }
        });
    }
}
