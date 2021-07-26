package pt.unl.fct.di.aldeia.apdc2021.ui.refresh;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;


public class RefreshActivity extends AppCompatActivity {

    private RefreshViewModel refreshViewModel;
    private RefreshActivity mActivity;
    private UserLocalStore storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage=new UserLocalStore(this);
        mActivity=this;

        refreshViewModel = new ViewModelProvider(this, new RefreshViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(RefreshViewModel.class);

        refreshViewModel.getRefreshResult().observe(this, new Observer<RefreshResult>() {
            @Override
            public void onChanged(@Nullable RefreshResult refreshResult) {
                if (refreshResult == null) {
                    return;
                }
                if (refreshResult.getError() != null) {
                    showRefreshFailed(refreshResult.getError());
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED,intent);
                    finish();
                }
                if (refreshResult.getSuccess() != null) {
                    updateUiWithUser(refreshResult.getSuccess());
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }

            }
        });

        UserAuthenticated user=storage.getLoggedInUser();
        refreshViewModel.refreshToken(user.getEmail(), user.getRefreshToken());
    }

    private void updateUiWithUser(UserAuthenticated model) {
        storage.storeUserData(model);
        storage.setUserLoggedIn(true);
    }

    private void showRefreshFailed(@StringRes Integer errorString) {
        storage.clearUserData();
    }


}