package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SearchEventsService {
    @POST("rest/searchEventsByRange")
    Call<SearchEventsReply> searchEvents(@Body SearchEventsData data);

}
