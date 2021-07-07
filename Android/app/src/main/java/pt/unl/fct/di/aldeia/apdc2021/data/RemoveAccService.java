package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.TokenCredentials;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RemoveAccService {
    @POST("rest/update/remove")
    Call<Void> removeUser(@Body TokenCredentials user);
}
