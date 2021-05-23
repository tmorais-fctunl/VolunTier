package pt.unl.fct.di.aldeia.apdc2021.ui.recoverPassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.RecoverPwRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

public class RecoverPwViewModel extends ViewModel {

    private MutableLiveData<RecoverPwFormState> recoverPwFormState = new MutableLiveData<>();
    private MutableLiveData<RecoverPwResult> recoverPwResult = new MutableLiveData<>();
    private RecoverPwRepository recoverPwRepository;


    private final Executor executor;

    RecoverPwViewModel(RecoverPwRepository recoverPwRepository, Executor executor) {
        this.executor = executor;
        this.recoverPwRepository = recoverPwRepository;
    }

    LiveData<RecoverPwFormState> getRecoverPwFormState() {
        return recoverPwFormState;
    }

    LiveData<RecoverPwResult> getRecoverPwResult() {
        return recoverPwResult;
    }


    public void recoverPassword(String username, String email) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = recoverPwRepository.recoverPassword(username, email);
                if (result instanceof Result.Success) {
                    UserAuthenticated data = ((Result.Success<UserAuthenticated>) result).getData();
                    recoverPwResult.postValue(new RecoverPwResult(R.string.recoverPw_success,null));
                } else {
                    recoverPwResult.postValue(new RecoverPwResult(null,R.string.recoverPw_failed));
                }
            }
        });
    }

    public void recoverPwDataChanged(String username, String email) {
        boolean somethingWrong=false;
        Integer usernameError=null;
        Integer emailError=null;
        if (!isUserNameValid(username)) {
            usernameError=R.string.invalid_username;
            somethingWrong=true;
        }
        if (!isEmailValid(email)) {
            emailError=R.string.invalid_email;
            somethingWrong=true;
        }
        if(somethingWrong){
            recoverPwFormState.setValue(new RecoverPwFormState(usernameError,emailError));
        }
        else {
            recoverPwFormState.setValue(new RecoverPwFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username.length()>4) {
            return true;
        }
        return false;
    }

    // A placeholder email validation check
    private boolean isEmailValid(String email){
        return email.matches(".+@.+[.].+");
    }

}
