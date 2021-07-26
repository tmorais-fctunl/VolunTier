package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RecoverPwCredentials;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RecoverPwService {
    @POST("rest/forgotpassword")
    Call<Void> recoverPassword(@Body RecoverPwCredentials user);


}
