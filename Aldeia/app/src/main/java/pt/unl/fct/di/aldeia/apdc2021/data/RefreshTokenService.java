package pt.unl.fct.di.aldeia.apdc2021.data;


import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.TokenCredentials;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RefreshTokenService {
    @POST("rest/refresh")
    Call<UserAuthenticated> refreshUser(@Body TokenCredentials user);
}