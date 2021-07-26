package pt.unl.fct.di.aldeia.apdc2021.ui.changePassword;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginFormState;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginViewModelFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    private UserLocalStore storage;
    private ChangePasswordViewModel changePasswordViewModel;
    private ChangePasswordActivity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        storage= new UserLocalStore(this);
        changePasswordViewModel = new ViewModelProvider(this, new ChangePasswordViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(ChangePasswordViewModel.class);
        mActivity=this;
        final EditText currentPwEditText = findViewById(R.id.editTextChangePWCurrentPassword);
        final EditText passwordEditText = findViewById(R.id.editTextChangePWNewPassword);
        final EditText passwordConfirmationEditText = findViewById(R.id.editTextChangePWNewPasswordConfirmation);
        final Button changePWButton =findViewById(R.id.changePWButton) ;
        final ProgressBar changePWLoading = findViewById(R.id.loadingChangePW);

        changePasswordViewModel.getChangePasswordFormState().observe(this, new Observer<ChangePasswordFormState>() {
            @Override
            public void onChanged(@Nullable ChangePasswordFormState changePasswordFormState) {
                if (changePasswordFormState == null) {
                    return;
                }
                changePWButton.setEnabled(changePasswordFormState.isDataValid());
                if (!passwordEditText.getText().toString().equals("") && changePasswordFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(changePasswordFormState.getPasswordError()));
                }
                if (!passwordConfirmationEditText.getText().toString().equals("") && changePasswordFormState.getPasswordConfirmationError() != null) {
                    passwordConfirmationEditText.setError(getString(changePasswordFormState.getPasswordConfirmationError()));
                }
            }
        });

        changePasswordViewModel.getChangePasswordResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(@Nullable DefaultResult changePasswordResult) {
                if (changePasswordResult == null) {
                    return;
                }
                if (changePasswordResult.getError() != null) {
                    Toast.makeText(mActivity, "Password Update Unsuccessful", Toast.LENGTH_SHORT).show();
                    changePWLoading.setVisibility(View.GONE);
                }
                if (changePasswordResult.getSuccess() != null) {
                    Toast.makeText(mActivity, "Password Update Succeeded", Toast.LENGTH_SHORT).show();
                    finish();
                }
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
                changePasswordViewModel.changePWDataChanged(currentPwEditText.getText().toString(),
                        passwordEditText.getText().toString(),passwordConfirmationEditText.getText().toString());
            }
        };
        passwordConfirmationEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        changePWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePWLoading.setVisibility(View.VISIBLE);
                UserAuthenticated user= storage.getLoggedInUser();
                changePasswordViewModel.changePassword(user.getEmail(),user.getTokenID(),user.getEmail(),encryptThisString(currentPwEditText.getText().toString())
                ,encryptThisString(passwordEditText.getText().toString()),encryptThisString(passwordConfirmationEditText.getText().toString()));
            }
        });


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