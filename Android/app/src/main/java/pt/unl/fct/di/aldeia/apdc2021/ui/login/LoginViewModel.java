package pt.unl.fct.di.aldeia.apdc2021.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.LoginRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

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

    public void loginDataChanged(String username, String password) {
        boolean somethingWrong=false;
        Integer usernameError=null;
        Integer passwordError=null;
        if (!isUserNameValid(username)) {
            usernameError=R.string.invalid_username;
            somethingWrong=true;
        }if (!isPasswordValid(password)) {
            passwordError=R.string.invalid_password;
            somethingWrong=true;
        } if(somethingWrong){
            loginFormState.setValue(new LoginFormState(usernameError,passwordError));
        }
        else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username.length()>4) {
            return true;
        }
        return false;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 7;
    }
}