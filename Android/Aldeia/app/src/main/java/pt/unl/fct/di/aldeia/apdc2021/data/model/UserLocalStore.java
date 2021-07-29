package pt.unl.fct.di.aldeia.apdc2021.data.model;

import android.content.SharedPreferences;
import android.content.Context;
import android.net.Uri;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Set;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(UserAuthenticated user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("email", user.getEmail());
        userLocalDatabaseEditor.putString("access_token", user.accessToken);
        userLocalDatabaseEditor.putString("refresh_token",user.refreshToken);
        userLocalDatabaseEditor.putLong("access_token_expiration_date", user.expirationDate);
        userLocalDatabaseEditor.putLong("refresh_token_expiration_date", user.refresh_expirationDate);
        userLocalDatabaseEditor.commit();
    }

    public void storeUserFullData(UserFullData user){
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        //userLocalDatabaseEditor.putString("email", user.getEmail());
        userLocalDatabaseEditor.putString("username", user.getUsername());
        userLocalDatabaseEditor.putString("fullName",user.getFullName());
        userLocalDatabaseEditor.putString("profile", user.getProfile());
        userLocalDatabaseEditor.putString("landline", user.getLandline());
        userLocalDatabaseEditor.putString("mobile",user.getMobile());
        userLocalDatabaseEditor.putString("address", user.getAddress());
        userLocalDatabaseEditor.putString("address2", user.getAddress2());
        userLocalDatabaseEditor.putString("region",user.getRegion());
        userLocalDatabaseEditor.putString("zipcode", user.getPc());
        userLocalDatabaseEditor.putString("website", user.getWebsite());
        userLocalDatabaseEditor.putString("facebook",user.getFacebook());
        userLocalDatabaseEditor.putString("instagram", user.getInstagram());
        userLocalDatabaseEditor.putString("twitter",user.getTwitter());
        userLocalDatabaseEditor.putString("pic_64",user.getPic());
        userLocalDatabaseEditor.putStringSet("events_participating", user.getEvents_participating());
        userLocalDatabaseEditor.putStringSet("events_created", user.getEvents_created());
        userLocalDatabaseEditor.putFloat("currentCurrency", user.getCurrentCurrency());
        userLocalDatabaseEditor.commit();
    }
    public void decreaseCoins(float amount) {
        float currentCurrency = userLocalDatabase.getFloat("currentCurrency", 0);
        currentCurrency-=amount;
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putFloat("currentCurrency", currentCurrency);
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

    public void setImagePath(String imagePath){
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("imagePath", imagePath);
        userLocalDatabaseEditor.commit();
    }

    public String getImagePath(){
        if (!userLocalDatabase.getBoolean("loggedIn", false)) {
            return null;
        }
        String imagePath = userLocalDatabase.getString("imagePath", "");
        return imagePath;
    }

    public UserAuthenticated getLoggedInUser() {
        if (!userLocalDatabase.getBoolean("loggedIn", false)) {
            return null;
        }

        String email = userLocalDatabase.getString("email", "");
        String accessToken = userLocalDatabase.getString("access_token", "");
        String refreshToken = userLocalDatabase.getString("refresh_token", "");
        Long accessTokenExpirationDate = userLocalDatabase.getLong("access_token_expiration_date", 0);
        Long refreshTokenExpirationDate = userLocalDatabase.getLong("refresh_token_expiration_date", 0);
        Long creationDate = userLocalDatabase.getLong("creation_date", 0);
        UserAuthenticated user = new UserAuthenticated(email, accessToken, refreshToken, creationDate, accessTokenExpirationDate,  refreshTokenExpirationDate);
        return user;
    }

    public UserFullData getUserFullData() {
        String email = userLocalDatabase.getString("email", "");
        String username = userLocalDatabase.getString("username", "");
        String full_name = userLocalDatabase.getString("fullName", "");
        String profile = userLocalDatabase.getString("profile", "");
        String landline = userLocalDatabase.getString("landline", "");
        String mobile = userLocalDatabase.getString("mobile", "");
        String address = userLocalDatabase.getString("address", "");
        String address2 = userLocalDatabase.getString("address2", "");
        String region = userLocalDatabase.getString("region", "");
        String zipcode = userLocalDatabase.getString("zipcode", "");
        String website = userLocalDatabase.getString("website", "");
        String facebook = userLocalDatabase.getString("facebook", "");
        String instagram = userLocalDatabase.getString("instagram", "");
        String twitter = userLocalDatabase.getString("twitter", "");
        String pic_64 = userLocalDatabase.getString("pic_64", "");
        Set<String> events_participating = userLocalDatabase.getStringSet("events_participating", null);
        Set<String> events_created = userLocalDatabase.getStringSet("events_created", null);
        float currentCurrency = userLocalDatabase.getFloat("currentCurrency", 0);

        //TODO
        UserFullData user  = new UserFullData(username, email,  full_name, profile, landline,
                mobile, address, address2,  region, zipcode, website,  facebook,  instagram, twitter, pic_64,
                events_participating, events_created, currentCurrency);
        return user;
    }
}