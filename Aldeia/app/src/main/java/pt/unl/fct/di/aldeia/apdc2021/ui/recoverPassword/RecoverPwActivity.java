package pt.unl.fct.di.aldeia.apdc2021.ui.recoverPassword;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginActivity;


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


public class RecoverPwActivity extends AppCompatActivity {

    private RecoverPwViewModel recoverPwViewModel;
    private RecoverPwActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_pw);



        mActivity = this;
        recoverPwViewModel = new ViewModelProvider(this, new RecoverPwViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(RecoverPwViewModel.class);

        final EditText emailEditText = findViewById(R.id.recoverPw_email);
        final Button recoverPwButton = findViewById(R.id.recoverPw_button);
        final ProgressBar loading =findViewById(R.id.recoverPw_ProgressBar);

        recoverPwViewModel.getRecoverPwFormState().observe(this, new Observer<RecoverPwFormState>() {
            @Override
            public void onChanged(@Nullable RecoverPwFormState recoverPwFormState) {
                if (recoverPwFormState == null) {
                    return;
                }
                recoverPwButton.setEnabled(recoverPwFormState.isDataValid());

                if (!emailEditText.getText().toString().equals("") && recoverPwFormState.getEmailError() != null) {
                    emailEditText.setError(getString(recoverPwFormState.getEmailError()));
                }
            }
        });

        recoverPwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                recoverPwViewModel.recoverPassword(emailEditText.getText().toString());
                recoverPwButton.setEnabled(false);

            }
        });

        recoverPwViewModel.getRecoverPwResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(@Nullable DefaultResult recoverPwResult) {
                if (recoverPwResult == null) {
                    return;
                }
                loading.setVisibility(View.GONE);
                if (recoverPwResult.getError() != null) {
                    showRecoveryFailed(recoverPwResult.getError());
                }
                if (recoverPwResult.getSuccess() != null) {
                    confirmRecovery(recoverPwResult.getSuccess());
                    setResult(Activity.RESULT_OK);
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
                recoverPwViewModel.recoverPwDataChanged(emailEditText.getText().toString());
            }
        };

        emailEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    recoverPwViewModel.recoverPassword(emailEditText.getText().toString());
                }
                return false;
            }
        });

    }
    private void confirmRecovery(@StringRes Integer successString) {
        String welcome = getString(R.string.recoverPw_success);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showRecoveryFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

}