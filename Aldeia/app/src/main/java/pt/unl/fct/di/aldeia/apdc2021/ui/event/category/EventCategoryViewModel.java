package pt.unl.fct.di.aldeia.apdc2021.ui.event.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventParticipantsRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.SearchEventCategoryRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsCategoryReply;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetParticipants.GetEventParticipantsResult;

public class EventCategoryViewModel extends ViewModel {
    private final SearchEventCategoryRepository repository;
    private final MutableLiveData<GetSearchEventCategoryResult> getSearchEventCategoryResult = new MutableLiveData<>();
    private final Executor executor;
    EventCategoryViewModel(SearchEventCategoryRepository repository, Executor executor) {
        this.executor = executor;
        this.repository = repository;
    }

    public LiveData<GetSearchEventCategoryResult> getSearchEventCategoryResult() {
        return getSearchEventCategoryResult;
    }

    public void getSearchEventCategory(String email, String token, String cursor, String category) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<SearchEventsCategoryReply> result = repository.searchEventsCategory(email, token, cursor, category);
                if (result instanceof Result.Success) {
                    SearchEventsCategoryReply data = ((Result.Success<SearchEventsCategoryReply>) result).getData();
                    getSearchEventCategoryResult.postValue(new GetSearchEventCategoryResult(data, null));
                } else {
                    getSearchEventCategoryResult.postValue(new GetSearchEventCategoryResult(null, R.string.lookUp_fail));
                }
            }
        });
    }
}