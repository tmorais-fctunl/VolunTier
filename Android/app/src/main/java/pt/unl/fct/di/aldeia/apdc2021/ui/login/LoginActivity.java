package pt.unl.fct.di.aldeia.apdc2021.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.databinding.ActivityLoginBinding;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.recoverPassword.RecoverPwActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.refresh.RefreshActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.register.RegisterActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.validate.ValidateActivity;

public class LoginActivity extends AppCompatActivity {
    private static final int VALIDATION=1;
    private static final int REFRESH=2;
    private LoginViewModel loginViewModel;
    private LoginActivity mActivity;
    private ActivityLoginBinding binding;
    private UserLocalStore storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        storage= new UserLocalStore(this);
        mActivity = this;
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(LoginViewModel.class);

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final TextView registerButton = findViewById(R.id.redirectRegister);
        final TextView recoverPwButton = findViewById(R.id.recoverPw);
        final ProgressBar loadingProgressBar = binding.loading;

        if(storage.getLoggedInUser()!=null){
            Intent intent = new Intent(mActivity, ValidateActivity.class);
            startActivityForResult(intent,VALIDATION);
        }

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (!emailEditText.getText().toString().equals("") && loginFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (!passwordEditText.getText().toString().equals("") && loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    loadingProgressBar.setVisibility(View.GONE);
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    UserAuthenticated user =loginResult.getSuccess();
                    loginViewModel.lookUp(user.getEmail(), user.getTokenID());
                    storeUserAuthData(user);
                }

                //Complete and destroy login activity once successful
                //finish();
            }
        });

        loginViewModel.getLookUpResult().observe(this, new Observer<LookUpResult>() {
            @Override
            public void onChanged(@Nullable LookUpResult lookUpResult) {
                if (lookUpResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (lookUpResult.getError() != null) {
                }
                if (lookUpResult.getSuccess() != null) {
                    UserFullData userData =lookUpResult.getSuccess();
                    updateUserFullData(userData);
                    Intent intent = new Intent(mActivity, MainLoggedInActivity.class);
                    startActivity(intent);
                    finish();
                }

                //Complete and destroy login activity once successful
                //finish();
            }
        });



        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, RegisterActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        recoverPwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, RecoverPwActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }

    private void storeUserAuthData(UserAuthenticated model) {
        storage.storeUserData(model);
        storage.setUserLoggedIn(true);

    }

    private void updateUserFullData(UserFullData user){
        String welcome = getString(R.string.welcome) + user.getEmail();
        // TODO : initiate successful logged in experience
        storage.storeUserFullData(user);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {

        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        UserAuthenticated user=storage.getLoggedInUser();
        if(requestCode==VALIDATION )
        {
            if(resultCode==Activity.RESULT_OK){
                loginViewModel.lookUp(user.getEmail(), user.getTokenID());
                Intent intent = new Intent(mActivity,MainLoggedInActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Intent intent = new Intent(mActivity, RefreshActivity.class);
                startActivityForResult(intent,REFRESH);
            }
        }
        if(requestCode==REFRESH){
            if(resultCode==Activity.RESULT_OK){
                loginViewModel.lookUp(user.getEmail(), user.getTokenID());
                Intent intent = new Intent(mActivity,MainLoggedInActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

}