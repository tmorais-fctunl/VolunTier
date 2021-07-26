package pt.unl.fct.di.aldeia.apdc2021.data;

import android.util.EventLog;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventID;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddEventDataSource {
    private final AddEventService service;

    public AddEventDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(AddEventService.class);
    }

    public Result<EventID> addEvent(CreateEventData event) {
        Call<EventID> addEventCall = service.addEvent(event);
        try {
            Response<EventID> response = addEventCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            if (response.code() == 429) {
                return new  Result.Error(new Exception("429"));
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error creating event", e));
        }
    }

}
