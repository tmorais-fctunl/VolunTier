package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventID;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddEventService {
    @POST("rest/addEvent")
    Call<EventID> addEvent(@Body CreateEventData event);
}
