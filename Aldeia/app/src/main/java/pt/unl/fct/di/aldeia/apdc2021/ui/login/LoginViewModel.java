package pt.unl.fct.di.aldeia.apdc2021.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.LoginRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.ui.loadingScreen.LookUpResult;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    private final MutableLiveData<LookUpResult> lookUpResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    private final Executor executor;

    LoginViewModel(LoginRepository loginRepository, Executor executor) {
        this.executor = executor;
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    LiveData<LookUpResult> getLookUpResult(){return lookUpResult;}

    public void login(String username, String password) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<UserAuthenticated> result = loginRepository.login(username, password);
                if (result instanceof Result.Success) {
                    UserAuthenticated data = ((Result.Success<UserAuthenticated>) result).getData();
                    loginResult.postValue(new LoginResult(data));
                } else {
                    loginResult.postValue(new LoginResult(R.string.login_failed));
                }
            }
        });
    }

    public void lookUp(String email, String token, String target) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<UserFullData> result = loginRepository.lookUp(email, token, target);
                if (result instanceof Result.Success) {
                    UserFullData data = ((Result.Success<UserFullData>) result).getData();
                    lookUpResult.postValue(new LookUpResult(data, null));
                } else {
                    lookUpResult.postValue(new LookUpResult(null, R.string.lookUp_fail));
                }
            }
        });
    }

    public void loginDataChanged(String email, String password) {
        boolean somethingWrong=false;
        Integer emailError=null;
        Integer passwordError=null;
        if (!isEmailValid(email)) {
            emailError=R.string.invalid_login_credential;
            somethingWrong=true;
        }if (!isPasswordValid(password)) {
            passwordError=R.string.invalid_password;
            somethingWrong=true;
        } if(somethingWrong){
            loginFormState.setValue(new LoginFormState(emailError,passwordError));
        }
        else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder email validation check
    private boolean isEmailValid(String email){
        return email.length()>5;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 7;
    }
}