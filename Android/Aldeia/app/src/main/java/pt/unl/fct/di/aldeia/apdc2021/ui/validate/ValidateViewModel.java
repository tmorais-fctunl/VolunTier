package pt.unl.fct.di.aldeia.apdc2021.ui.validate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;

import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.ValidateTokenRepository;



public class ValidateViewModel extends ViewModel {

    private final ValidateTokenRepository validateRepository;
    private final MutableLiveData<DefaultResult> validateResult = new MutableLiveData<>();
    private final Executor executor;
    ValidateViewModel(ValidateTokenRepository validateRepository, Executor executor) {
        this.executor = executor;
        this.validateRepository = validateRepository;
    }

    LiveData<DefaultResult> getValidateResult() {
        return validateResult;
    }

    public void validateToken(String username, String token) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = validateRepository.validate(username, token);
                if (result instanceof Result.Success) {
                    validateResult.postValue(new DefaultResult(R.string.login_success, null));
                } else {
                    validateResult.postValue(new DefaultResult(null, R.string.login_failed));
                }
            }
        });
    }
}
