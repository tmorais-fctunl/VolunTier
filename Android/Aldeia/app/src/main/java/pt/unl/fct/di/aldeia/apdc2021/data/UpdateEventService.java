package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventUpdateData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UpdateEventService {
    @POST("rest/updateEvent/attributes")
    Call<Void> updateEvent(@Body EventUpdateData event);
}
