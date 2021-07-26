package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ParticipateEventService {
    @POST("rest/participateEvent")
    Call<Void> participateInEvent(@Body GetEventData event);
}
