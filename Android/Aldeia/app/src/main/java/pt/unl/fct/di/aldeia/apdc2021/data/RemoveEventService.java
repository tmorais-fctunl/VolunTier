package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RemoveEventService {
    @POST("/rest/updateEvent/remove")
    Call<Void> removeEvent(@Body GetEventData event);
}