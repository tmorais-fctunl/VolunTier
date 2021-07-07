package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.MainLoggedInRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventID;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventMarkerData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;

public class MainLoggedInViewModel extends ViewModel {
    private final MainLoggedInRepository repository;
    private final MutableLiveData<UserFullData> userFullData;
    private final MutableLiveData<UserAuthenticated> userAuth;
    private final MutableLiveData<LogoutResult> logoutResult;
    private final MutableLiveData<UpdateDataResult> updateDataResult;
    private final MutableLiveData<MainLoggedInActivityTransitionHandler> transitionHandler;
    private final MutableLiveData<UpdateProfileDataFormState> profileFormState;
    private final MutableLiveData<UserUpdateData> userUpdate;
    private final MutableLiveData<RemoveAccResult> removeAccResult;
    private final MutableLiveData<AddEventResult> addEventResult;
    private LatLng eventCoordinates;
    private String event_id;

    private final Executor executor;


    MainLoggedInViewModel(MainLoggedInRepository mainLoggedInRepository, Executor executor) {
        this.userFullData = new MutableLiveData<UserFullData>();
        this.removeAccResult= new MutableLiveData<RemoveAccResult>();
        this.userAuth = new MutableLiveData<UserAuthenticated>();
        this.logoutResult= new MutableLiveData<LogoutResult>();
        this.transitionHandler = new MutableLiveData<MainLoggedInActivityTransitionHandler>();
        this.profileFormState = new MutableLiveData<UpdateProfileDataFormState>();
        this.updateDataResult = new MutableLiveData<UpdateDataResult>();
        this.userUpdate = new MutableLiveData<UserUpdateData>();
        this.addEventResult = new MutableLiveData<AddEventResult>();
        this.executor = executor;
        this.repository=mainLoggedInRepository;
        this.eventCoordinates = null;
        this.event_id = null;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public LatLng getEventCoordinates () {
        return eventCoordinates;
    }
    public void setEventCoordinates (LatLng latLng) {
        eventCoordinates = latLng;
    }

    public void setUserAuthData(UserAuthenticated userAuth){
        this.userAuth.postValue(userAuth);
    }

    public UserAuthenticated getUserAuth(){
        return this.userAuth.getValue();
    }

    public void setUserFullData(UserFullData userFullData) {
        this.userFullData.postValue(userFullData);
    }

    public UserFullData getUserFullData(){
        return userFullData.getValue();
    }

    public void setUserUpdate(UserUpdateData userUpdate) {
        this.userUpdate.postValue(userUpdate);
    }

    public UserUpdateData getUserUpdateData(){
        return userUpdate.getValue();
    }
    public LiveData<UserFullData> getUserFullDataLD(){
        return userFullData;
    }

    public void setTransitionHandler(MainLoggedInActivityTransitionHandler transitionHandler) {
        this.transitionHandler.postValue(transitionHandler);
    }

    public MutableLiveData<MainLoggedInActivityTransitionHandler> getTransitionHandler() {
        return transitionHandler;
    }
    public LiveData<LogoutResult> getLogoutResult() {
        return logoutResult;
    }
    public LiveData<UpdateDataResult> getUpdateDataResult() {
        return updateDataResult;
    }
    public LiveData<RemoveAccResult> getRemoveAccResult(){return removeAccResult;}
    public LiveData<UpdateProfileDataFormState> getProfileFormState() {
        return profileFormState;
    }
    public LiveData<AddEventResult> getAddEventResult() {
        return addEventResult;
    }

    public void logout(String username, String token) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = repository.logout(username, token);
                if (result instanceof Result.Success) {
                    logoutResult.postValue(new LogoutResult(R.string.logout_success, null));
                } else {
                    logoutResult.postValue(new LogoutResult(null, R.string.logout_failed));
                }
            }
        });
    }
    public void updateProfileData(UserUpdateData user) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = repository.updateProfileData(user);
                if (result instanceof Result.Success) {
                    updateDataResult.postValue(new UpdateDataResult(R.string.DataChange_success, null));
                } else {
                    updateDataResult.postValue(new UpdateDataResult(null, R.string.DataChange_failed));
                }
            }
        });
    }

    public void removeAccount(String email,String token) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = repository.removeAccDataSource(email, token);
                if (result instanceof Result.Success) {
                    removeAccResult.postValue(new RemoveAccResult(R.string.RemoveAcc_success, null));
                } else {
                    removeAccResult.postValue(new RemoveAccResult(null, R.string.RemoveAcc_failed));
                }
            }
        });
    }

    public void createEvent (CreateEventData event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<EventID> result = repository.addEventDataSource(event);
                if (result instanceof Result.Success) {
                    addEventResult.postValue(new AddEventResult(new EventMarkerData(((Result.Success<EventID>) result).getData().getEventID(), event.getPoint()), null));
                } else {
                    addEventResult.postValue(new AddEventResult(null, R.string.AddEvent_failed));
                }
            }
        });
    }

    public void updateProfileDataChanged(String postalCode, String mobile, String landLine, String address, String region, String fullName) {
        boolean somethingWrong=false;
        Integer postalCodeError=null;
        Integer mobileError=null;
        Integer landLineError=null;
        Integer addressError=null;
        Integer regionError=null;
        Integer fullNameError=null;
        if (!isPostalCodeValid(postalCode)) {
            postalCodeError=R.string.postalCode_Error;
            somethingWrong=true;
        }if (!isMobileValid(mobile)) {
            mobileError=R.string.mobile_Error;
            somethingWrong=true;
        }if (!isLandLineValid(landLine)) {
            landLineError=R.string.landLine_Error;
            somethingWrong=true;
        }if (!isAddressValid(address)) {
            addressError=R.string.address_Error;
            somethingWrong=true;
        }if (!isRegionValid(region)) {
            regionError=R.string.region_Error;
            somethingWrong=true;
        }if (!isFullNameValid(fullName)) {
            fullNameError=R.string.fullName_Error;
            somethingWrong=true;
        } if(somethingWrong){
            profileFormState.setValue(new UpdateProfileDataFormState(postalCodeError,mobileError,landLineError,addressError,regionError,fullNameError));
        }
        else {
            profileFormState.setValue(new UpdateProfileDataFormState(true));
        }
    }

    private boolean isPostalCodeValid(String postalCode){
        return postalCode.matches("[0-9]{4}-[0-9]{3}") || postalCode.equals("");
    }

    private boolean isMobileValid(String mobile){
        return mobile.matches("([+][0-9]{2,3}\\s)?[789][0-9]{8}") || mobile.equals("");
    }

    private boolean isLandLineValid(String landLine){
        return landLine.matches("([+][0-9]{2,3}\\s)?[2][0-9]{8}") || landLine.equals("");
    }

    private boolean isAddressValid(String address){
        return address.length()>5 || address.equals("");
    }

    private boolean isRegionValid(String region){
        return region.length()>3 || region.equals("");
    }
    private boolean isFullNameValid(String fullName){
        return fullName.length()<120 || fullName.equals("");
    }
}
