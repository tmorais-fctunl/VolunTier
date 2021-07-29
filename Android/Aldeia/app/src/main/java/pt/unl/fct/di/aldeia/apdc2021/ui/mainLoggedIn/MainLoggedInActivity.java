package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventMarkerData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.ui.changePassword.ChangePasswordActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.CommunityProfileActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetEventActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.login.LoginActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.refresh.RefreshActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.route.GetRouteActivity;

public class MainLoggedInActivity extends AppCompatActivity implements RemoveAccDialog.RemoveAccDialogListener, CreateEventDialog.EventDialogListener,CreateRouteDialog.RouteDialogListener{
    private static final int REFRESH=2;
    private static final int EVENT=4;
    private static final int ROUTE=9;
    private static final int PICK_IMAGE = 100;
    private UserLocalStore storage;
    private MainLoggedInViewModel viewModel;
    private RoomViewModel roomViewModel;
    private MainLoggedInActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=this;
        // viewModel = new ViewModelProvider(this).get(MainLoggedInViewModel.class);
        viewModel= new ViewModelProvider(this, new MainLoggedInViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(MainLoggedInViewModel.class);
        roomViewModel=new RoomViewModel(getApplication());
        roomViewModel.clear();
        storage = new UserLocalStore(this);
        UserAuthenticated userAuthenticated= storage.getLoggedInUser();
        UserFullData userFullData = storage.getUserFullData();
        viewModel.setUserAuthData(userAuthenticated);
        viewModel.setUserFullData(userFullData);
        viewModel.setImagePath(storage.getImagePath());
        setContentView(R.layout.test);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();
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
                                ,userUpdate.getAddress(),userUpdate.getAddress2(),userUpdate.getRegion(),userUpdate.getPc(),userUpdate.getWebsite(),userUpdate.getFacebook(),userUpdate.getInstagram(), userUpdate.getTwitter(), userFullData.getPic(),
                                userFullData.getEvents_participating(), userFullData.getEvents_created(), userFullData.getCurrentCurrency());
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
                    case "Create Route":
                        CreateRouteDialog createRoute = new CreateRouteDialog();
                        createRoute.show(mActivity.getSupportFragmentManager(), "");
                        break;
                    case "Event Info":
                        Intent intent1 = new Intent(mActivity, GetEventActivity.class);
                        intent1.putExtra("event_id", viewModel.getEvent_id());
                        startActivityForResult(intent1 , EVENT);
                        break;
                    case "Route Info":
                        Intent intentRoute = new Intent(mActivity, GetRouteActivity.class);
                        intentRoute.putExtra("route_id", viewModel.getRoute_id());
                        startActivityForResult(intentRoute , ROUTE);
                        break;
                    case "Edit Photo":
                        Intent intentPhoto = new Intent();
                        intentPhoto.setType("image/*");
                        intentPhoto.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intentPhoto, "Select Picture"), PICK_IMAGE);
                        break;
                    case "Look up":
                        Intent intentLookUp = new Intent(mActivity, CommunityProfileActivity.class);
                        intentLookUp.putExtra("target", viewModel.getUserToLookUp());
                        intentLookUp.putExtra("email", storage.getLoggedInUser().getEmail());
                        intentLookUp.putExtra("token", storage.getLoggedInUser().getTokenID());
                        startActivity(intentLookUp);
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
        });
        viewModel.getLogoutResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(@Nullable DefaultResult logoutResult) {
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
        viewModel.getUpdateDataResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(@Nullable DefaultResult updateDataResult) {
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

        viewModel.getRemoveAccResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(@Nullable DefaultResult removeAccResult) {
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

        viewModel.getUpdatePhotoResult().observe(this, new Observer<UpdatePhotoResult>() {
            @Override
            public void onChanged(@Nullable UpdatePhotoResult updatePhotoResult) {
                if (updatePhotoResult == null) {
                    return;
                }
                if (updatePhotoResult.getError() != null) {
                    Toast.makeText(mActivity, updatePhotoResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if (updatePhotoResult.getSuccess() != null) {
                    /*try {
                        File f = new File(viewModel.getImagePath(), "profile.jpg");
                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] photoByteArray = stream.toByteArray();
                        // then do something about the response
                        viewModel.updatePhotoGC(photoByteArray,updatePhotoResult.getSuccess().toString());


                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }*/
                    Toast.makeText(mActivity, "Photo was successfully updated.", Toast.LENGTH_SHORT).show();
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
        if(requestCode==EVENT)
        {
            if(resultCode == Activity.RESULT_OK){
                String operation=data.getStringExtra("operation");
                if (operation.equals("Delete")){
                    viewModel.setDeletedEvent(data.getStringExtra("id"));
                }
                else if(operation.equals("Update")){
                    double[] coords =  {data.getDoubleExtra("lat", 0), data.getDoubleExtra("lon", 0)};
                    EventMarkerData event= new EventMarkerData(data.getStringExtra("id"),coords);
                    viewModel.setUpdatedEvent(event);
                }
            }
        }
        if(requestCode==ROUTE)
        {
            if(resultCode == Activity.RESULT_OK){
                String operation=data.getStringExtra("operation");
                if (operation.equals("Delete")){
                    viewModel.setDeletedRoute(data.getStringExtra("id"));
                }
            }
        }
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageURI= data.getData();
            InputStream inputStream;
            try{
                UserAuthenticated user =storage.getLoggedInUser();
                inputStream=getContentResolver().openInputStream(imageURI);
                Bitmap image= BitmapFactory.decodeStream(inputStream);
                Bitmap resizedImage=Bitmap.createScaledBitmap(image,200,200,false);


                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                // Create imageDir
                File mypath=new File(directory,"profile.jpg");

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                storage.setImagePath(directory.getAbsolutePath());
                viewModel.setImagePath(directory.getAbsolutePath());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                resizedImage.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                String finalImage=Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
                String encodedString = "data:image/png;base64," + finalImage;
                viewModel.updatePhoto(new UpdatePhotoData(user.getEmail(),user.getTokenID(),encodedString));

            }catch(FileNotFoundException e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        UserAuthenticated user = storage.getLoggedInUser();
        viewModel.removeAccount(user.getEmail(), user.getTokenID(), user.getEmail());
    }

    @Override
    public void applyTexts(String eventName, String startDate, String endDate, String description, String category, String profile, String contact, int capacity, int difficulty) {
        UserAuthenticated user = storage.getLoggedInUser();
        LatLng latLng = viewModel.getEventCoordinates();
        double[] coordinates = {latLng.latitude, latLng.longitude};
        CreateEventData eventInfo = new CreateEventData(user.getEmail(), user.getTokenID(),
                eventName, coordinates, startDate, endDate, description, category, profile, contact, capacity,difficulty);
        viewModel.createEvent(eventInfo);
    }

    @Override
    public void dismissDialog() {
        final ImageButton createEvent = findViewById(R.id.createEventButton);
        viewModel.setTransitionHandler(new MainLoggedInActivityTransitionHandler(""));
        createEvent.setVisibility(View.VISIBLE);;
    }

    @Override
    public void createRoute(String route_name, String description) {
        UserAuthenticated user = storage.getLoggedInUser();
        viewModel.createRoute(new CreateRouteData(user.getEmail(),user.getTokenID(),viewModel.getNewRouteEvents(),route_name,description));
    }
}