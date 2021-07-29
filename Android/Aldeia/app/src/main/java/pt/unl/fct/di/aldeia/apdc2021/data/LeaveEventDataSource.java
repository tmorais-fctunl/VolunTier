package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RemoveParticipantFromEventData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LeaveEventDataSource {
    private final LeaveEventService service;

    public LeaveEventDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(LeaveEventService.class);
    }

    public Result<Void> leaveEvent(RemoveParticipantFromEventData data) {
        Call<Void> leaveEventCall = service.removeParticipant(data);
        try {
            Response<Void> response = leaveEventCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error leaving event", e));
        }
    }
}
