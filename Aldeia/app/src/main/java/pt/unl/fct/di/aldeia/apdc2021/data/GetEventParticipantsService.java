package pt.unl.fct.di.aldeia.apdc2021.data;


import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetEventParticipantsService {
    @POST("rest/getParticipants")
    Call<GetParticipantsReply> getEventParticipants(@Body GetEventParticipantsData event);
}
