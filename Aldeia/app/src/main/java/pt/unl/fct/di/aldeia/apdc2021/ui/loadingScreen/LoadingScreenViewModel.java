package pt.unl.fct.di.aldeia.apdc2021.ui.loadingScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.LoadingScreenRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;

public class LoadingScreenViewModel extends ViewModel {
    private final MutableLiveData<LookUpResult> lookUpResult = new MutableLiveData<>();
    private final LoadingScreenRepository loadingScreenRepository;

    private final Executor executor;

    LoadingScreenViewModel(LoadingScreenRepository loadingScreenRepository, Executor executor) {
        this.executor = executor;
        this.loadingScreenRepository=loadingScreenRepository;
    }


    LiveData<LookUpResult> getLookUpResult(){return lookUpResult;}



    public void lookUp(String email, String token, String target) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<UserFullData> result = loadingScreenRepository.lookUp(email, token, target);
                if (result instanceof Result.Success) {
                    UserFullData data = ((Result.Success<UserFullData>) result).getData();
                    lookUpResult.postValue(new LookUpResult(data, null));
                } else {
                    lookUpResult.postValue(new LookUpResult(null, R.string.lookUp_fail));
                }
            }
        });
    }
}
