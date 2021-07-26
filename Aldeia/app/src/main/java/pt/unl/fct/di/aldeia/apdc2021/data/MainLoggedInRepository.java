package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AllCausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.DonationInfo;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventID;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.ReceivedSearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteID;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchRoutesReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.TokenCredentials;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;

public class MainLoggedInRepository {
    private static volatile MainLoggedInRepository instance;

    private final ChangeProfileDataSource changeProfileDataSource;
    private final LogoutDataSource logoutDataSource;
    private final RemoveAccDataSource removeAccDataSource;
    private final AddEventDataSource addEventDataSource;
    private final UpdatePhotoDataSource updatePhotoDataSource;
    private final UploadPhotoGCDataSource uploadPhotoGCDataSource;
    private final SearchEventsDataSource searchEventsDataSource;
    private final PresencesRankDataSource presencesRankDataSource;
    private final PointsRankDataSource pointsRankDataSource;
    private final SearchUserDataSource searchUserDataSource;
    private final SearchRoutesDataSource searchRoutesDataSource;
    private final LookUpDataSource lookUpDataSource;
    private final GetAllCausesDataSource getAllCausesDataSource;
    private final DonateDataSource donateDataSource;
    private final AddRouteDataSource addRouteDataSource;


    // private constructor : singleton access
    private MainLoggedInRepository(ChangeProfileDataSource changeProfileDataSource, LogoutDataSource logoutDataSource
            , RemoveAccDataSource removeAccDataSource, AddEventDataSource addEventDataSource
    , UpdatePhotoDataSource updatePhotoDataSource, UploadPhotoGCDataSource uploadPhotoGCDataSource,SearchEventsDataSource searchEventsDataSource
            , PointsRankDataSource pointsRankDataSource, PresencesRankDataSource presencesRankDataSource, SearchUserDataSource searchUserDataSource
            , SearchRoutesDataSource searchRoutesDataSource, LookUpDataSource lookUpDataSource, GetAllCausesDataSource getAllCausesDataSource,
                                   DonateDataSource donateDataSource,AddRouteDataSource addRouteDataSource) {
        this.updatePhotoDataSource=updatePhotoDataSource;
        this.changeProfileDataSource=changeProfileDataSource;
        this.logoutDataSource=logoutDataSource;
        this.removeAccDataSource=removeAccDataSource;
        this.addEventDataSource = addEventDataSource;
        this.uploadPhotoGCDataSource = uploadPhotoGCDataSource;
        this.searchEventsDataSource=searchEventsDataSource;
        this.presencesRankDataSource = presencesRankDataSource;
        this.pointsRankDataSource = pointsRankDataSource;
        this.searchUserDataSource = searchUserDataSource;
        this.searchRoutesDataSource = searchRoutesDataSource;
        this.lookUpDataSource = lookUpDataSource;
        this.getAllCausesDataSource = getAllCausesDataSource;
        this.donateDataSource = donateDataSource;
        this.addRouteDataSource=addRouteDataSource;
    }

    public static MainLoggedInRepository getInstance(ChangeProfileDataSource changeProfileDataSource, LogoutDataSource logoutDataSource
            , RemoveAccDataSource removeAccDataSource, AddEventDataSource addEventDataSource, UpdatePhotoDataSource updatePhotoDataSource
            , UploadPhotoGCDataSource uploadPhotoGCDataSource,SearchEventsDataSource searchEventsDataSource,PointsRankDataSource pointsRankDataSource,
                                                     PresencesRankDataSource presencesRankDataSource, SearchUserDataSource searchUserDataSource,
                                                     SearchRoutesDataSource searchRoutesDataSource,LookUpDataSource lookUpDataSource,
                                                     GetAllCausesDataSource getAllCausesDataSource, DonateDataSource donateDataSource,AddRouteDataSource addRouteDataSource) {
        if (instance == null) {
            instance = new MainLoggedInRepository(changeProfileDataSource,logoutDataSource,removeAccDataSource, addEventDataSource
                    ,updatePhotoDataSource, uploadPhotoGCDataSource,searchEventsDataSource,pointsRankDataSource, presencesRankDataSource,
                    searchUserDataSource,searchRoutesDataSource, lookUpDataSource, getAllCausesDataSource, donateDataSource,addRouteDataSource);
        }
        return instance;
    }
    public Result<Void> logout(String username, String token) {
        return logoutDataSource.logout(username, token);
    }

    public Result<Void> updateProfileData(UserUpdateData user){
        return changeProfileDataSource.changeProfileAttributes(user);
    }

    public Result<Void> removeAccDataSource(String email,String token, String target){
        return removeAccDataSource.removeUser(email,token, target);
    }

    public Result<EventID> addEventDataSource (CreateEventData event) {
        return addEventDataSource.addEvent(event);
    }

    public Result<UpdatePhotoReply> updatePhoto(UpdatePhotoData data){
        return updatePhotoDataSource.updateEvent(data);
    }
    public Result<Void> updatePhotoGC(String url,byte[]data){
        return uploadPhotoGCDataSource.uploadPhotoGC(data,url);
    }

    public Result<SearchEventsReply> searchEvents(SearchEventsData data){
        return searchEventsDataSource.searchEvent(data);
    }

    public Result<RankingReceivedData> getPresencesRanking(RankingRequestData data) {
        return presencesRankDataSource.lookUpRanking(data);
    }

    public Result<RankingReceivedData> getPointsRanking(RankingRequestData data) {
        return pointsRankDataSource.lookUpPointsRanking(data);
    }

    public Result<SearchUserData> searchUser(ReceivedSearchUserData data, String username) {
        return searchUserDataSource.searchUser(data, username);
    }

    public Result<SearchRoutesReply> searchRoutes(SearchEventsData data){
        return searchRoutesDataSource.searchRoutes(data);
    }

    public Result<UserFullData> lookUp(String email, String token, String target) {
        return lookUpDataSource.lookUp(email, token, target);
    }

    public Result<AllCausesData> getAllCauses(String email, String token) {
        return getAllCausesDataSource.getAllCauses(new TokenCredentials(email, token));
    }

    public Result<Void> donate(String email, String token, String cause_id, float amount) {
        return donateDataSource.donate(new DonationInfo(token, email, cause_id, amount));
    }

    public Result<RouteID> createRoute(CreateRouteData data) {
        return addRouteDataSource.addRoute(data);
    }

}
