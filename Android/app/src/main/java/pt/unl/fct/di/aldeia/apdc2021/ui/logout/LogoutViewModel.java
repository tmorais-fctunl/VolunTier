package pt.unl.fct.di.aldeia.apdc2021.ui.logout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.LogoutRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;

public class LogoutViewModel extends ViewModel {
    private LogoutRepository logoutRepository;
    private MutableLiveData<LogoutResult> logoutResult = new MutableLiveData<>();
    private final Executor executor;
    LogoutViewModel(LogoutRepository logoutRepository, Executor executor) {
        this.executor = executor;
        this.logoutRepository= logoutRepository;
    }

    LiveData<LogoutResult> getLogoutResult() {
        return logoutResult;
    }

    public void logout(String username, String token) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = logoutRepository.logout(username, token);
                if (result instanceof Result.Success) {
                    logoutResult.postValue(new LogoutResult(R.string.logout_success, null));
                } else {
                    logoutResult.postValue(new LogoutResult(null, R.string.logout_failed));
                }
            }
        });
    }
}
