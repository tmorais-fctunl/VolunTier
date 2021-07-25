package pt.unl.fct.di.aldeia.apdc2021.ui.changePassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.ChangePasswordRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginFormState;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginResult;

public class ChangePasswordViewModel extends ViewModel {


    private final MutableLiveData<ChangePasswordFormState> changePasswordFormState = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> changePasswordResult = new MutableLiveData<>();
    private final ChangePasswordRepository changePasswordRepository;

    private final Executor executor;

    ChangePasswordViewModel(ChangePasswordRepository changePasswordRepository, Executor executor) {
        this.executor = executor;
        this.changePasswordRepository = changePasswordRepository;
    }

    LiveData<ChangePasswordFormState> getChangePasswordFormState() {
        return changePasswordFormState;
    }

    LiveData<DefaultResult> getChangePasswordResult() {
        return changePasswordResult;
    }

    public void changePassword(String email, String token,String target,String oldPassword,String password,String passwordConfirmation) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = changePasswordRepository.changePassword(email, token,target,oldPassword,password,passwordConfirmation);
                if (result instanceof Result.Success) {
                    changePasswordResult.postValue(new DefaultResult(R.string.ChangePW_success,null));
                } else {
                    changePasswordResult.postValue(new DefaultResult(null,R.string.ChangePW_failed));
                }
            }
        });
    }
    public void changePWDataChanged(String oldPassword ,String password, String passwordConfirmation) {
        boolean somethingWrong=false;
        Integer passwordConfirmationError=null;
        Integer passwordError=null;
        if (!isPasswordValid(password,oldPassword)) {
            passwordError=R.string.ChangePW_invalidPassword;
            somethingWrong=true;
        }if (!doPasswordsMatch(password,passwordConfirmation)) {
            passwordConfirmationError=R.string.invalid_pwConfirmation;
            somethingWrong=true;
        } if(somethingWrong){
            changePasswordFormState.setValue(new ChangePasswordFormState(passwordError,passwordConfirmationError));
        }
        else {
            changePasswordFormState.setValue(new ChangePasswordFormState(true));
        }
    }


    private boolean isPasswordValid(String password, String oldPassword) {
        return password != null && password.trim().length() > 7 && !password.equals(oldPassword);
    }

    private boolean doPasswordsMatch(String password, String newPassword) {
        return password != null && password.equals(newPassword);
    }

}
