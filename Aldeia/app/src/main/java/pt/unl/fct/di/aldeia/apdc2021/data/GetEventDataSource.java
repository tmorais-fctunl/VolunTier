package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserCredentials;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class GetEventDataSource {

    private final GetEventService service;

    public GetEventDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(GetEventService.class);
    }

    public Result<EventFullData> getEvent(String email, String token, String event_id) {
        //String encPassword= DigestUtils.sha512Hex(password);
        Call<EventFullData> eventFullDataCall = service.getEvent(new GetEventData(email, token, event_id));
        try {
            Response<EventFullData> response = eventFullDataCall.execute();
            if(response.isSuccessful()) {
                EventFullData ua = response.body();
                return new Result.Success<>(ua);
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error getting event", e));
        }
    }
}