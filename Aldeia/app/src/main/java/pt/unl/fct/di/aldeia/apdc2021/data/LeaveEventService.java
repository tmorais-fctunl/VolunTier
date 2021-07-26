package pt.unl.fct.di.aldeia.apdc2021.data;


import pt.unl.fct.di.aldeia.apdc2021.data.model.RemoveParticipantFromEventData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LeaveEventService {
    @POST("rest/removeParticipant")
    Call<Void> removeParticipant(@Body RemoveParticipantFromEventData event);
}
