package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserChangePassword;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChangeProfileService {
    @POST("rest/update/profile")
    Call<Void> updateProfile(@Body UserUpdateData user);

    @POST("rest/update/profile")
    Call<Void> changePassword(@Body UserChangePassword user);

}
