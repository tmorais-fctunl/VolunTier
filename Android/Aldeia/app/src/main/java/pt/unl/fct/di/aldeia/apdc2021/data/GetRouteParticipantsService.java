package pt.unl.fct.di.aldeia.apdc2021.data;


import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteParticipantsData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetRouteParticipantsService {
    @POST("rest/route/participants")
    Call<GetParticipantsReply> getRouteParticipants(@Body GetRouteParticipantsData route);
}