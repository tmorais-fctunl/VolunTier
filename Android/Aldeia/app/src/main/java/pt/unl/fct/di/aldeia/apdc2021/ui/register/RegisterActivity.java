package pt.unl.fct.di.aldeia.apdc2021.ui.register;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.databinding.ActivityLoginBinding;

import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginActivity;


public class RegisterActivity extends AppCompatActivity {


    private RegisterViewModel registerViewModel;
    private RegisterActivity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register);

        mActivity = this;
        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(RegisterViewModel.class);

        final EditText usernameEditText = findViewById(R.id.registerUsername);
        final EditText passwordEditText = findViewById(R.id.registerPassword1);
        final EditText password2EditText = findViewById(R.id.registerPassword2);
        final EditText emailEditText = findViewById(R.id.registerEmail);
        final Button registerButton = findViewById(R.id.registerButton);
        final ProgressBar loadingProgressBar = findViewById(R.id.register_ProgressBar);



        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                registerButton.setEnabled(registerFormState.isDataValid());
                if (!usernameEditText.getText().toString().equals("") && registerFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(registerFormState.getUsernameError()));
                }
                if (!passwordEditText.getText().toString().equals("") && registerFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registerFormState.getPasswordError()));
                }
                if (!emailEditText.getText().toString().equals("") && registerFormState.getEmailError() != null) {
                    emailEditText.setError(getString(registerFormState.getEmailError()));
                }
                if (!password2EditText.getText().toString().equals("") && registerFormState.getPassword2Error() != null) {
                    password2EditText.setError(getString(registerFormState.getPassword2Error()));
                }
            }
        });


        registerViewModel.getRegisterResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(@Nullable DefaultResult registerResult) {
                if (registerResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (registerResult.getError() != null) {
                    showRegisterFailed(registerResult.getError());
                }
                if (registerResult.getSuccess() != null) {
                    setResult(Activity.RESULT_OK);
                    showRegisterSuccess();
                    Intent intent = new Intent(mActivity, LoginActivity.class);
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
                registerViewModel.registerDataChanged(usernameEditText.getText().toString(),
                        emailEditText.getText().toString(),passwordEditText.getText().toString(),
                        password2EditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        password2EditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerViewModel.register(usernameEditText.getText().toString(),
                            emailEditText.getText().toString(),passwordEditText.getText().toString(),
                            password2EditText.getText().toString());

                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                registerViewModel.register(usernameEditText.getText().toString(),
                        emailEditText.getText().toString(),encryptThisString(passwordEditText.getText().toString()),
                        encryptThisString(password2EditText.getText().toString()));
            }
        });
    }

    private void showRegisterSuccess() {
        String welcome = getString(R.string.new_registration);
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showRegisterFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private static String encryptThisString(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}