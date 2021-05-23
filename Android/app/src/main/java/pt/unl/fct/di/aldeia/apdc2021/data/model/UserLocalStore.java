package pt.unl.fct.di.aldeia.apdc2021.data.model;

import android.content.SharedPreferences;
import android.content.Context;

import org.apache.commons.codec.digest.DigestUtils;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(UserAuthenticated user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("user_id", user.getUsername());
        userLocalDatabaseEditor.putString("access_token", user.accessToken);
        userLocalDatabaseEditor.putString("refresh_token",user.refreshToken);
        userLocalDatabaseEditor.putLong("access_token_expiration_date", user.expirationDate);
        userLocalDatabaseEditor.putLong("refresh_token_expiration_date", user.refresh_expirationDate);
        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public UserAuthenticated getLoggedInUser() {
        if (!userLocalDatabase.getBoolean("loggedIn", false)) {
            return null;
        }

        String username = userLocalDatabase.getString("user_id", "");
        String accessToken = userLocalDatabase.getString("access_token", "");
        String refreshToken = userLocalDatabase.getString("refresh_token", "");
        Long accessTokenExpirationDate = userLocalDatabase.getLong("access_token_expiration_date", 0);
        Long refreshTokenExpirationDate = userLocalDatabase.getLong("refresh_token_expiration_date", 0);
        Long creationDate = userLocalDatabase.getLong("creation_date", 0);
        UserAuthenticated user = new UserAuthenticated(username, accessToken, refreshToken, creationDate, accessTokenExpirationDate,  refreshTokenExpirationDate);
        return user;
    }
}