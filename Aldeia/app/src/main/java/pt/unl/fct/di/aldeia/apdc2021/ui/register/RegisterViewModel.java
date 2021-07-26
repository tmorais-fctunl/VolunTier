package pt.unl.fct.di.aldeia.apdc2021.ui.register;


import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.data.RegisterRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.R;

public class RegisterViewModel extends ViewModel {
    public static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9][.]?[a-zA-Z0-9]+$";
    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> registerResult = new MutableLiveData<>();
    private final RegisterRepository registerRepository;

    private final Executor executor;


    RegisterViewModel(RegisterRepository registerRepository, Executor executor) {
        this.executor = executor;
        this.registerRepository = registerRepository;

    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<DefaultResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String username,String email, String password,String password2) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = registerRepository.register(username,email, password);
                if (result instanceof Result.Success) {
                    registerResult.postValue(new DefaultResult(R.string.register_successes,null));
                } else {
                    registerResult.postValue(new DefaultResult(null,R.string.register_failed));
                }
            }
        });
    }

    public void registerDataChanged(String username, String email, String password, String password2) {
        boolean somethingWrong=false;
        Integer usernameError=null;
        Integer emailError=null;
        Integer passwordError=null;
        Integer password2Error=null;
        if (!isUsernameValid(username)) {
            usernameError=R.string.invalid_username;
            somethingWrong=true;
        }
        if (!isEmailValid(email)) {
            emailError=R.string.invalid_email;
            somethingWrong=true;
        }
        if (!isPasswordValid(password,password2)) {
            passwordError=R.string.invalid_password;
            somethingWrong=true;
        }
        if (!isConfirmationValid(password,password2)) {
            password2Error=R.string.invalid_pwConfirmation;
            somethingWrong=true;
        }
        if(somethingWrong){
            registerFormState.setValue(new RegisterFormState(usernameError,emailError,passwordError,password2Error));
        }
        else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }



    // A placeholder username validation check
    public static boolean isUsernameValid(String username) {
        return (username != null && username.length() > 4 && username.length() < 30) && username.matches(USERNAME_REGEX);
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password, String password2) {
        return password != null && password.trim().length() > 7 && password.trim().length()<33;
    }
    private boolean isConfirmationValid(String password, String password2){
        return password.trim().equals(password2.trim());
    }

    private boolean isEmailValid(String email){
        return email.matches(".+@.+[.].+");
    }
}