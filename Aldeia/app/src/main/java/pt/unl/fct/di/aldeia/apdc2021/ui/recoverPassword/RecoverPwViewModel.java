package pt.unl.fct.di.aldeia.apdc2021.ui.recoverPassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.RecoverPwRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

public class RecoverPwViewModel extends ViewModel {

    private final MutableLiveData<RecoverPwFormState> recoverPwFormState = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> recoverPwResult = new MutableLiveData<>();
    private final RecoverPwRepository recoverPwRepository;


    private final Executor executor;

    RecoverPwViewModel(RecoverPwRepository recoverPwRepository, Executor executor) {
        this.executor = executor;
        this.recoverPwRepository = recoverPwRepository;
    }

    LiveData<RecoverPwFormState> getRecoverPwFormState() {
        return recoverPwFormState;
    }

    LiveData<DefaultResult> getRecoverPwResult() {
        return recoverPwResult;
    }


    public void recoverPassword(String email) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = recoverPwRepository.recoverPassword(email);
                if (result instanceof Result.Success) {
                    UserAuthenticated data = ((Result.Success<UserAuthenticated>) result).getData();
                    recoverPwResult.postValue(new DefaultResult(R.string.recoverPw_success,null));
                } else {
                    recoverPwResult.postValue(new DefaultResult(null,R.string.recoverPw_failed));
                }
            }
        });
    }

    public void recoverPwDataChanged(String email) {
        boolean somethingWrong=false;
        Integer emailError=null;
        if (!isEmailValid(email)) {
            emailError=R.string.invalid_email;
            somethingWrong=true;
        }
        if(somethingWrong){
            recoverPwFormState.setValue(new RecoverPwFormState(emailError));
        }
        else {
            recoverPwFormState.setValue(new RecoverPwFormState(true));
        }
    }

    // A placeholder email validation check
    private boolean isEmailValid(String email){
        return email.matches(".+@.+[.].+");
    }

}
