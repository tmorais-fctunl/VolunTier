package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import okhttp3.Route;
import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.MainLoggedInRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.Room.EventEntity;
import pt.unl.fct.di.aldeia.apdc2021.data.model.AllCausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventID;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventMarkerData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.ReceivedSearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteID;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchRoutesReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.LookUpResultCommunity;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.SearchUserResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.category.GetSearchEventCategoryResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.leaderboard.RankRequestResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.shop.AllCausesResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.shop.DonationResult;

public class MainLoggedInViewModel extends ViewModel {
    private final MainLoggedInRepository repository;
    private final MutableLiveData<UserFullData> userFullData;
    private final MutableLiveData<UserAuthenticated> userAuth;
    private String imagePath;
    private String userToLookUp;
    private final MutableLiveData<DefaultResult> logoutResult;
    private final MutableLiveData<DefaultResult> updateDataResult;
    private final MutableLiveData<MainLoggedInActivityTransitionHandler> transitionHandler;
    private final MutableLiveData<UpdateProfileDataFormState> profileFormState;
    private final MutableLiveData<UserUpdateData> userUpdate;
    private final MutableLiveData<DefaultResult> removeAccResult;
    private final MutableLiveData<AddEventResult> addEventResult;
    private final MutableLiveData<UpdatePhotoResult> updatePhotoResult;
    private final MutableLiveData<SearchEventsResult> searchEventsResult;
    private final MutableLiveData<CreateEventFormState> createEventFormState;
    private final MutableLiveData<String> deletedEvent;
    private final MutableLiveData<String> deletedRoute;
    private final MutableLiveData<EventMarkerData> updatedLocationEvent;
    private final MutableLiveData<RankRequestResult> rankRequestResult;
    private final MutableLiveData<SearchUserResult> searchUserResult;
    private final MutableLiveData<SearchRoutesResult> searchRoutesResult;
    private final MutableLiveData<LookUpResultCommunity> lookUpResultCommunity;
    private final MutableLiveData<AllCausesResult> allCausesResult;
    private final MutableLiveData<DonationResult> donationResult;
    private final MutableLiveData<AddRouteResult> addRouteResult;
    private LatLng eventCoordinates;
    private String event_id;
    private String route_id;
    private List<String> newRouteEvents;
    private final Executor executor;


    MainLoggedInViewModel(MainLoggedInRepository mainLoggedInRepository, Executor executor) {
        this.userFullData = new MutableLiveData<UserFullData>();
        this.removeAccResult= new MutableLiveData<DefaultResult>();
        this.userAuth = new MutableLiveData<UserAuthenticated>();
        this.logoutResult= new MutableLiveData<DefaultResult>();
        this.transitionHandler = new MutableLiveData<MainLoggedInActivityTransitionHandler>();
        this.profileFormState = new MutableLiveData<UpdateProfileDataFormState>();
        this.updateDataResult = new MutableLiveData<DefaultResult>();
        this.userUpdate = new MutableLiveData<UserUpdateData>();
        this.updatePhotoResult= new MutableLiveData<UpdatePhotoResult>();
        this.addEventResult = new MutableLiveData<AddEventResult>();
        this.searchEventsResult = new MutableLiveData<SearchEventsResult>();
        this.createEventFormState = new MutableLiveData<CreateEventFormState>();
        this.deletedEvent = new MutableLiveData<String>();
        this.updatedLocationEvent= new MutableLiveData<EventMarkerData>();
        this.rankRequestResult = new MutableLiveData<RankRequestResult>();
        this.searchUserResult = new MutableLiveData<SearchUserResult>();
        this.searchRoutesResult = new MutableLiveData<SearchRoutesResult>();
        this.lookUpResultCommunity = new MutableLiveData<LookUpResultCommunity>();
        this.allCausesResult = new MutableLiveData<AllCausesResult>();
        this.donationResult = new MutableLiveData<DonationResult>();
        this.addRouteResult = new MutableLiveData<AddRouteResult>();
        this.deletedRoute= new MutableLiveData<String>();
        this.executor = executor;
        this.repository=mainLoggedInRepository;
        this.eventCoordinates = null;
        this.event_id = null;
        this.route_id = null;
        this.imagePath=null;
        this.userToLookUp=null;
    }

    public String getUserToLookUp() {
        return userToLookUp;
    }

    public void setUserToLookUp(String userToLookUp) {
        this.userToLookUp=userToLookUp;
    }

    public String getImagePath(){
        return imagePath;
    }

    public void nukeSearchUserResult() {
        this.searchUserResult.setValue(null);
    }

    public void setImagePath(String image){
        imagePath=image;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id=route_id;
    }

    public String getRoute_id() {
        return route_id;
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

    public void setRankRequestResult() {
        this.rankRequestResult.setValue(null);
    }

    public UserAuthenticated getUserAuth(){
        return this.userAuth.getValue();
    }

    public void setUserFullData(UserFullData userFullData) {
        this.userFullData.postValue(userFullData);
    }
    public void setRouteCoordinates(List<String> newRouteEventIds) {
        newRouteEvents=newRouteEventIds;
    }

    public List<String> getNewRouteEvents() {
        return newRouteEvents;
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

    public LiveData<AddRouteResult> getAddRouteResult() {
        return addRouteResult;
    }
    public LiveData<DefaultResult> getLogoutResult() {
        return logoutResult;
    }
    public LiveData<DefaultResult> getUpdateDataResult() {
        return updateDataResult;
    }
    public LiveData<DefaultResult> getRemoveAccResult(){return removeAccResult;}
    public LiveData<UpdateProfileDataFormState> getProfileFormState() {
        return profileFormState;
    }
    public LiveData<CreateEventFormState> getCreateEventFormState() {
        return createEventFormState;
    }
    public LiveData<AddEventResult> getAddEventResult() {
        return addEventResult;
    }
    public LiveData<UpdatePhotoResult> getUpdatePhotoResult(){
        return updatePhotoResult;
    }
    public LiveData<SearchEventsResult> getSearchEventsResult(){
        return searchEventsResult;
    }
    public LiveData<RankRequestResult> getRankRequestResult() {
        return rankRequestResult;
    }
    public LiveData<SearchUserResult> getSearchUserResult() {
        return searchUserResult;
    }
    public LiveData<SearchRoutesResult> getSearchRoutesResult() {
        return searchRoutesResult;
    }

    public void setDeletedEvent(String id){
        deletedEvent.postValue(id);
    }
    public LiveData<String> getDeletedEvent(){
        return deletedEvent;
    }
    public void setUpdatedEvent(EventMarkerData data){
        updatedLocationEvent.postValue(data);
    }
    public LiveData<EventMarkerData> getUpdatedEvent(){
        return updatedLocationEvent;
    }

    public void setDeletedRoute(String id){
        deletedRoute.postValue(id);
    }
    public LiveData<String> getDeletedRoute(){
        return deletedRoute;
    }

    public void resetEventSearch(){searchEventsResult.setValue(null);}

    public void resetRoutesSearch(){searchRoutesResult.setValue(null);}

    public void resetAddRoute(){addRouteResult.setValue(null);}

    public void logout(String username, String token) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = repository.logout(username, token);
                if (result instanceof Result.Success) {
                    logoutResult.postValue(new DefaultResult(R.string.logout_success, null));
                } else {
                    logoutResult.postValue(new DefaultResult(null, R.string.logout_failed));
                }
            }
        });
    }

    public LiveData<LookUpResultCommunity> getLookUpResultCommunity() {
        return lookUpResultCommunity;
    }

    public LiveData<AllCausesResult> getAllCausesResult() {
        return allCausesResult;
    }

    public void resetAllCausesData() {
        this.allCausesResult.setValue(null);
    }

    public void getAllCauses(String email, String token) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<AllCausesData> result = repository.getAllCauses(email, token);
                if (result instanceof Result.Success) {
                    AllCausesData data = ((Result.Success<AllCausesData>) result).getData();
                    allCausesResult.postValue(new AllCausesResult(data, null));
                } else {
                    allCausesResult.postValue(new AllCausesResult(null, R.string.lookUp_fail));
                }
            }
        });
    }

    public LiveData<DonationResult> getDonationResult() {
        return donationResult;
    }

    public void resetDonationResult() {
        this.donationResult.setValue(null);
    }

    public void donate(String email, String token, String cause_id, float amount) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = repository.donate(email, token, cause_id, amount);
                if (result instanceof Result.Success) {
                    donationResult.postValue(new DonationResult(R.string.lookUp_success, null));
                } else {
                    donationResult.postValue(new DonationResult(null, R.string.lookUp_fail));
                }
            }
        });
    }

    public void nukeLookUpResultCommunity() {
        this.lookUpResultCommunity.setValue(null);
    }

    public void lookUpCommunity(String email, String token, String target) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<UserFullData> result = repository.lookUp(email, token, target);
                if (result instanceof Result.Success) {
                    UserFullData data = ((Result.Success<UserFullData>) result).getData();
                    lookUpResultCommunity.postValue(new LookUpResultCommunity(data, null));
                } else {
                    lookUpResultCommunity.postValue(new LookUpResultCommunity(null, R.string.lookUp_fail));
                }
            }
        });
    }

    public void getSearchUser(String token, String email, String[] cursor, String username) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<SearchUserData> result = repository.searchUser(new ReceivedSearchUserData(email, token, cursor),username);
                if (result instanceof Result.Success) {
                    searchUserResult.postValue(new SearchUserResult(((Result.Success<SearchUserData>) result).getData(), null));
                } else {
                    searchUserResult.postValue(new SearchUserResult(null, R.string.community_failed));
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
                    updateDataResult.postValue(new DefaultResult(R.string.DataChange_success, null));
                } else {
                    updateDataResult.postValue(new DefaultResult(null, R.string.DataChange_failed));
                }
            }
        });
    }

    public void removeAccount(String email,String token, String target) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = repository.removeAccDataSource(email, token, target);
                if (result instanceof Result.Success) {
                    removeAccResult.postValue(new DefaultResult(R.string.RemoveAcc_success, null));
                } else {
                    removeAccResult.postValue(new DefaultResult(null, R.string.RemoveAcc_failed));
                }
            }
        });
    }

    public void getRankingEvent(String token, String email, String cursor) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<RankingReceivedData> result = repository.getPresencesRanking(new RankingRequestData(email, token, cursor));
                if (result instanceof Result.Success) {
                    rankRequestResult.postValue(new RankRequestResult(((Result.Success<RankingReceivedData>) result).getData(), null));
                } else {
                    rankRequestResult.postValue(new RankRequestResult(null, R.string.leaderboard_failed));
                }
            }
        });
    }

    public void getRankingPoints(String token, String email, String cursor) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<RankingReceivedData> result = repository.getPointsRanking(new RankingRequestData(email, token, cursor));
                if (result instanceof Result.Success) {
                    rankRequestResult.postValue(new RankRequestResult(((Result.Success<RankingReceivedData>) result).getData(), null));
                } else {
                    rankRequestResult.postValue(new RankRequestResult(null, R.string.leaderboard_failed));
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
                    addEventResult.postValue(new AddEventResult(new EventEntity(((Result.Success<EventID>) result).getData().getEventID(),event.getPoint()[0],event.getPoint()[1],event.getEventName(),1,
                            event.getStartDate(),event.getEndDate(),GeoHashUtil.convertCoordsToGeoHashLowPrecision(event.getPoint()[0],event.getPoint()[1])), null));
                } else {
                    if(((Result.Error)result).getError().getMessage().equals("429")){
                        addEventResult.postValue(new AddEventResult(null, R.string.too_many_events_created_daily));
                    }else{
                        addEventResult.postValue(new AddEventResult(null, R.string.addEvent_failed));
                    }
                }
            }
        });
    }

    public void createRoute (CreateRouteData route) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<RouteID> result = repository.createRoute(route);
                if (result instanceof Result.Success) {
                    addRouteResult.postValue(new AddRouteResult(((Result.Success<RouteID>) result).getData(), null));
                } else {
                    if(((Result.Error)result).getError().getMessage().equals("429")){
                        addEventResult.postValue(new AddEventResult(null, R.string.too_many_routes_created_daily));
                    }else{
                        addEventResult.postValue(new AddEventResult(null, R.string.add_route_failed));
                    }

                }
            }
        });
    }

    public void updatePhoto(UpdatePhotoData data){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<UpdatePhotoReply> result = repository.updatePhoto(data);
                if (result instanceof Result.Success) {
                    updatePhotoResult.postValue(new UpdatePhotoResult(((Result.Success<UpdatePhotoReply>) result).getData() , null));
                } else {
                    updatePhotoResult.postValue(new UpdatePhotoResult(null, R.string.UpdatePhoto_failed));
                }
            }
        });
    }

    public void searchEvents(SearchEventsData data){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<SearchEventsReply> result = repository.searchEvents(data);
                if (result instanceof Result.Success) {
                    searchEventsResult.postValue(new SearchEventsResult(((Result.Success<SearchEventsReply>) result).getData() , null));
                } else {
                    searchEventsResult.postValue(new SearchEventsResult(null , R.string.SearchEvents_failed));
                }
            }
        });
    }

    public void searchRoutes(SearchEventsData data){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<SearchRoutesReply> result = repository.searchRoutes(data);
                if (result instanceof Result.Success) {
                    searchRoutesResult.postValue(new SearchRoutesResult(((Result.Success<SearchRoutesReply>) result).getData() , null));
                } else {
                    searchRoutesResult.postValue(new SearchRoutesResult(null , R.string.SearchEvents_failed));
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

    public void updatePhotoGC(byte[] data,String url ){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = repository.updatePhotoGC(url,data);
                if (result instanceof Result.Success) {
                }
            }
        });
    }

    public void createEventDataChanged(String event_name, String startDate, String endDate, String description, String contact) {
        boolean somethingWrong=false;
        Integer event_nameError=null;
        Integer dateError=null;
        Integer descriptionError=null;
        Integer contactError= null;
        if (!isEventNameValid(event_name)) {
            event_nameError=R.string.addEvent_name_error;
            somethingWrong=true;
        }if (!isDateValid(startDate, endDate)) {
            dateError=R.string.update_event_dateError;
            somethingWrong=true;
        }if (!isDescriptionValid(description)) {
            descriptionError=R.string.update_event_commentError;
            somethingWrong=true;
        }if (!isContactValid(contact)) {
            contactError=R.string.update_event_contactError;
            somethingWrong=true;
        }if(somethingWrong){
            createEventFormState.setValue(new CreateEventFormState(event_nameError, dateError, descriptionError, contactError));
        }
        else {
            createEventFormState.setValue(new CreateEventFormState(true));
        }
    }


    private boolean isContactValid(String contact) {
        return contact.equals("")||contact.matches("([+][0-9]{2,3}\\s)?[789][0-9]{8}");
    }

    private boolean isDateValid(String startDate, String endDate) {
        if (startDate.length()<17 || endDate.length()<17 ) {
            return false;
        }
        SimpleDateFormat formatD = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        String sDate = startDate.substring(0,startDate.indexOf("T"));
        sDate = sDate.concat("-").concat(startDate.substring(startDate.indexOf("T")+1, startDate.length()-4));
        String eDate = endDate.substring(0,endDate.indexOf("T"));
        eDate = eDate.concat("-").concat(endDate.substring(endDate.indexOf("T")+1, endDate.length()-4));
        try {
            int a = 2;
            Date sD = formatD.parse(sDate);
            Date eD = formatD.parse(eDate);
            if(sD.compareTo(eD) > 0) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean isDescriptionValid(String description) {
        return description.length()<500 && description.length()>0;
    }

    private boolean isEventNameValid(String event_name) {
        return event_name.length()>4;
    }



}
