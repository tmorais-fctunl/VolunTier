package pt.unl.fct.di.aldeia.apdc2021.ui.loadingScreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginViewModelFactory;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.MainLoggedInActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.refresh.RefreshActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.validate.ValidateActivity;

public class LoadingActivity extends AppCompatActivity {

    private static final int VALIDATION=1;
    private static final int REFRESH=2;
    private UserLocalStore storage;
    private LoadingActivity mActivity;
    private LoadingScreenViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        viewModel = new ViewModelProvider(this, new LoadingScreenViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(LoadingScreenViewModel.class);
        storage= new UserLocalStore(this);
        mActivity=this;
        if(storage.getLoggedInUser()!=null){
            Intent intent = new Intent(mActivity, ValidateActivity.class);
            startActivityForResult(intent,VALIDATION);
        }else{
            Intent intent = new Intent(mActivity, LoginActivity.class);
            startActivity(intent);
        }

        viewModel.getLookUpResult().observe(this, new Observer<LookUpResult>() {
            @Override
            public void onChanged(@Nullable LookUpResult lookUpResult) {
                if (lookUpResult == null) {
                    return;
                }
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        UserAuthenticated user=storage.getLoggedInUser();
        if(requestCode==VALIDATION )
        {
            if(resultCode== Activity.RESULT_OK){
                viewModel.lookUp(user.getEmail(), user.getTokenID(), user.getEmail());
            }
            else{
                Intent intent = new Intent(mActivity, RefreshActivity.class);
                startActivityForResult(intent,REFRESH);
            }
        }
        if(requestCode==REFRESH){
            if(resultCode==Activity.RESULT_OK){
                viewModel.lookUp(user.getEmail(), user.getTokenID(), user.getEmail());
            }else{
                Intent intent = new Intent(mActivity, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private void updateUserFullData(UserFullData user){
        String welcome = getString(R.string.welcome) + user.getEmail();
        storage.storeUserFullData(user);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_SHORT).show();
    }
}