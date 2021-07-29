package pt.unl.fct.di.aldeia.apdc2021.ui.validate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;

public class ValidateActivity extends AppCompatActivity {

    private ValidateViewModel validateViewModel;
    private ValidateActivity mActivity;
    private UserLocalStore storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage=new UserLocalStore(this);
        mActivity = this;

        validateViewModel = new ViewModelProvider(this, new ValidateViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(ValidateViewModel.class);

        validateViewModel.getValidateResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(@Nullable DefaultResult validateResult) {
                if (validateResult == null) {
                    return;
                }
                if (validateResult.getError() != null) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED,intent);
                    finish();
                }
                if (validateResult.getSuccess() != null) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }

            }
        });

        UserAuthenticated user = storage.getLoggedInUser();
        validateViewModel.validateToken(user.getEmail(), user.getTokenID());
    }
}