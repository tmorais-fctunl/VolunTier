package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventID;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;

public class MainLoggedInRepository {
    private static volatile MainLoggedInRepository instance;

    private final ChangeProfileDataSource changeProfileDataSource;
    private final LogoutDataSource logoutDataSource;
    private final RemoveAccDataSource removeAccDataSource;
    private final AddEventDataSource addEventDataSource;

    // private constructor : singleton access
    private MainLoggedInRepository(ChangeProfileDataSource changeProfileDataSource, LogoutDataSource logoutDataSource
            , RemoveAccDataSource removeAccDataSource, AddEventDataSource addEventDataSource) {
        this.changeProfileDataSource=changeProfileDataSource;
        this.logoutDataSource=logoutDataSource;
        this.removeAccDataSource=removeAccDataSource;
        this.addEventDataSource = addEventDataSource;

    }

    public static MainLoggedInRepository getInstance(ChangeProfileDataSource changeProfileDataSource, LogoutDataSource logoutDataSource
            , RemoveAccDataSource removeAccDataSource, AddEventDataSource addEventDataSource) {
        if (instance == null) {
            instance = new MainLoggedInRepository(changeProfileDataSource,logoutDataSource,removeAccDataSource, addEventDataSource);
        }
        return instance;
    }
    public Result<Void> logout(String username, String token) {
        return logoutDataSource.logout(username, token);
    }

    public Result<Void> updateProfileData(UserUpdateData user){
        return changeProfileDataSource.changeProfileAttributes(user);
    }

    public Result<Void> removeAccDataSource(String email,String token){
        return removeAccDataSource.removeUser(email,token);
    }

    public Result<EventID> addEventDataSource (CreateEventData event) {
        return addEventDataSource.addEvent(event);
    }

}
