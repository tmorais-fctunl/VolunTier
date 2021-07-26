package pt.unl.fct.di.aldeia.apdc2021.ui.refresh;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.RefreshTokenRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;


public class RefreshViewModel extends ViewModel {

    private final RefreshTokenRepository refreshRepository;
    private final MutableLiveData<RefreshResult> refreshResult = new MutableLiveData<>();
    private final Executor executor;
    RefreshViewModel(RefreshTokenRepository refreshRepository, Executor executor) {
        this.executor = executor;
        this.refreshRepository = refreshRepository;
    }

    LiveData<RefreshResult> getRefreshResult() {
        return refreshResult;
    }

    public void refreshToken(String username, String token) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<UserAuthenticated> result = refreshRepository.refreshToken(username, token);
                if (result instanceof Result.Success) {
                    UserAuthenticated data = ((Result.Success<UserAuthenticated>) result).getData();
                    refreshResult.postValue(new RefreshResult(data));
                } else {
                    refreshResult.postValue(new RefreshResult(R.string.login_failed));//TODO
                }
            }
        });
    }
}
