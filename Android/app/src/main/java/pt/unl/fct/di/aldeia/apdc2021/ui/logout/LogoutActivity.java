package pt.unl.fct.di.aldeia.apdc2021.ui.logout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.MapsActivity;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.refresh.RefreshActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.validate.ValidateActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.validate.ValidateResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.validate.ValidateViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.validate.ValidateViewModelFactory;

public class LogoutActivity extends AppCompatActivity {

    private static final int VALIDATION=1;
    private static final int REFRESH=2;
    private LogoutViewModel logoutViewModel;
    private LogoutActivity mActivity;
    private UserLocalStore storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage=new UserLocalStore(this);
        mActivity = this;

        logoutViewModel = new ViewModelProvider(this, new LogoutViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(LogoutViewModel.class);

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


        logoutViewModel.getLogoutResult().observe(this, new Observer<LogoutResult>() {
            @Override
            public void onChanged(@Nullable LogoutResult logoutResult) {
                if (logoutResult == null) {
                    return;
                }
                if (logoutResult.getError() != null) {
                    setResult(Activity.RESULT_CANCELED);
                    //does nothing
                }
                if (logoutResult.getSuccess() != null) {
                    setResult(Activity.RESULT_OK);
                    storage.clearUserData();
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        UserAuthenticated user = storage.getLoggedInUser();
        logoutViewModel.logout(user.getUsername(), user.getTokenID());
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==VALIDATION || requestCode==REFRESH)
        {
            if(resultCode != Activity.RESULT_OK){
                Intent intent = new Intent(mActivity,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}