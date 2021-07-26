package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GetPhotoService {
    @POST("rest/picture/{username}")
    Call<EventFullData> getProfilePhoto(@Body GetEventData event, @Path("username") String username);
}
