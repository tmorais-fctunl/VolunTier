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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.MapsActivity;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserCredentials;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.databinding.ActivityLoginBinding;
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

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final TextView registerButton = findViewById(R.id.redirectRegister);
        final TextView recoverPwButton = findViewById(R.id.recoverPw);
        final ProgressBar loadingProgressBar = binding.loading;

        if(storage.getLoggedInUser()!=null){
            UserAuthenticated user=storage.getLoggedInUser();
            if (System.currentTimeMillis()<user.getExpirationDate()){
                Intent intent = new Intent(mActivity, ValidateActivity.class);
                startActivityForResult(intent,VALIDATION);
            }
            else if(System.currentTimeMillis()<user.getRefresh_expirationDate()){
                Intent intent = new Intent(mActivity, RefreshActivity.class);
                startActivityForResult(intent,REFRESH);
            }
        }

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (!usernameEditText.getText().toString().equals("") && loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
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
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);
                    Intent intent = new Intent(mActivity, MapsActivity.class);
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
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        /*passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });*/

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
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

    private void updateUiWithUser(UserAuthenticated model) {
        String welcome = getString(R.string.welcome) + model.getUsername();
        // TODO : initiate successful logged in experience
        storage.storeUserData(model);
        storage.setUserLoggedIn(true);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {

        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==VALIDATION || requestCode==REFRESH)
        {
            if(resultCode==Activity.RESULT_OK){
                Intent intent = new Intent(mActivity,MapsActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

}