package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.ui.changePassword.ChangePasswordActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetEventActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.refresh.RefreshActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.validate.ValidateActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainLoggedInActivity extends AppCompatActivity implements RemoveAccDialog.RemoveAccDialogListener, CreateEventDialog.EventDialogListener{
    private static final int VALIDATION=1;
    private static final int REFRESH=2;
    private UserLocalStore storage;
    private MainLoggedInViewModel viewModel;
    private MainLoggedInActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=this;
        // viewModel = new ViewModelProvider(this).get(MainLoggedInViewModel.class);
        viewModel= new ViewModelProvider(this, new MainLoggedInViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(MainLoggedInViewModel.class);
        storage = new UserLocalStore(this);
        UserAuthenticated userAuthenticated= storage.getLoggedInUser();
        UserFullData userFullData = storage.getUserFullData();
        viewModel.setUserAuthData(userAuthenticated);
        viewModel.setUserFullData(userFullData);
        setContentView(R.layout.test);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();
        //NavController navController = Navigation.findNavController(this,  R.id.fragmentContainerView);
        // AppBarConfiguration appBarConfiguration=AppBarConfiguration
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        viewModel.getTransitionHandler().observe(this, new Observer<MainLoggedInActivityTransitionHandler>() {
            @Override
            public void onChanged(@Nullable MainLoggedInActivityTransitionHandler transitionHandler) {
                UserAuthenticated user=storage.getLoggedInUser();
                if(System.currentTimeMillis()<user.getRefresh_expirationDate() && System.currentTimeMillis()>user.getExpirationDate()){
                    Intent intent = new Intent(mActivity, RefreshActivity.class);
                    startActivityForResult(intent,REFRESH);
                }
                switch (transitionHandler.getTransition()){
                    case "Logout":
                        viewModel.logout(userAuthenticated.getEmail(),userAuthenticated.getTokenID());
                        break;
                    case "Update Data":
                        UserUpdateData userUpdate=viewModel.getUserUpdateData();
                        viewModel.updateProfileData(userUpdate);
                        UserFullData useraux = new UserFullData(userFullData.getUsername(),userFullData.getEmail(),userUpdate.getFullName(),userUpdate.getProfile(),userUpdate.getLandline(),userUpdate.getMobile()
                                ,userUpdate.getAddress(),userUpdate.getAddress2(),userUpdate.getRegion(),userUpdate.getPc(),userUpdate.getWebsite(),userUpdate.getFacebook(),userUpdate.getInstagram(), userUpdate.getTwitter());
                        storage.storeUserFullData(useraux);
                        viewModel.setUserFullData(useraux);
                        break;
                    case "Remove":
                        RemoveAccDialog dialog = new RemoveAccDialog();
                        dialog.show(mActivity.getSupportFragmentManager(),"");
                        break;
                    case "Change Password":
                        Intent intent = new Intent(mActivity, ChangePasswordActivity.class);
                        startActivity(intent);
                        break;
                    case "Create Event":
                        CreateEventDialog create = new CreateEventDialog();
                        create.show(mActivity.getSupportFragmentManager(), "");
                        break;
                    case "Event Info":
                        Intent intent1 = new Intent(mActivity, GetEventActivity.class);
                        intent1.putExtra("event_id", viewModel.getEvent_id());
                        startActivity(intent1);
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
        });
        viewModel.getLogoutResult().observe(this, new Observer<LogoutResult>() {
            @Override
            public void onChanged(@Nullable LogoutResult logoutResult) {
                if (logoutResult == null) {
                    return;
                }
                if (logoutResult.getError() != null) {
                    setResult(Activity.RESULT_CANCELED);
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
        viewModel.getUpdateDataResult().observe(this, new Observer<UpdateDataResult>() {
            @Override
            public void onChanged(@Nullable UpdateDataResult updateDataResult) {
                if (updateDataResult == null) {
                    return;
                }
                if (updateDataResult.getError() != null) {
                    Toast.makeText(mActivity, updateDataResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if (updateDataResult.getSuccess() != null) {
                    Toast.makeText(mActivity, updateDataResult.getSuccess(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        viewModel.getRemoveAccResult().observe(this, new Observer<RemoveAccResult>() {
            @Override
            public void onChanged(@Nullable RemoveAccResult removeAccResult) {
                if (removeAccResult == null) {
                    return;
                }
                if (removeAccResult.getError() != null) {
                    Toast.makeText(mActivity, removeAccResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if (removeAccResult.getSuccess() != null) {
                    setResult(Activity.RESULT_OK);
                    Toast.makeText(mActivity, removeAccResult.getSuccess(), Toast.LENGTH_SHORT).show();
                    storage.clearUserData();
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        viewModel.getUserFullDataLD().observe(this, new Observer<UserFullData>() {
            @Override
            public void onChanged(@Nullable UserFullData user) {
                storage.storeUserFullData(user);
            }
        });

        viewModel.getAddEventResult().observe(this, new Observer<AddEventResult>() {
            @Override
            public void onChanged(@Nullable AddEventResult addEventResult) {
                if (addEventResult == null) {
                    return;
                }
                if (addEventResult.getError() != null) {
                    Toast.makeText(mActivity, addEventResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if (addEventResult.getSuccess() != null) {
                    Toast.makeText(mActivity, "Event was successfully created.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REFRESH)
        {
            if(resultCode != Activity.RESULT_OK){
                Intent intent = new Intent(mActivity, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        UserAuthenticated user = storage.getLoggedInUser();
        viewModel.removeAccount(user.getEmail(), user.getTokenID());
    }

    @Override
    public void applyTexts(String eventName, String startDate, String endDate) {
        UserAuthenticated user = storage.getLoggedInUser();
        LatLng latLng = viewModel.getEventCoordinates();
        double[] coordinates = {latLng.latitude, latLng.longitude};
        CreateEventData eventInfo = new CreateEventData(user.getEmail(), user.getTokenID(),
                eventName, coordinates, startDate, endDate);
        viewModel.createEvent(eventInfo);
    }

    @Override
    public void dismissDialog() {
        final ImageButton createEvent = findViewById(R.id.createEventButton);
        final TextView newEvent = findViewById(R.id.eventText);
        newEvent.setVisibility(View.VISIBLE);
        createEvent.setVisibility(View.VISIBLE);
    }
}