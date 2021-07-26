package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.ProfileTokenCredentials;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LookUpService {
    @POST("rest/user")
    Call<UserFullData> lookUpUser(@Body ProfileTokenCredentials user);
}