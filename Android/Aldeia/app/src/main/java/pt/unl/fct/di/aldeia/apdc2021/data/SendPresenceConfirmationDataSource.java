package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.SendQRCodeData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendPresenceConfirmationDataSource {
    private final SendPresenceConfirmationService service;

    public SendPresenceConfirmationDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(SendPresenceConfirmationService.class);
    }

    public Result<Void> sendPresenceConfirmation(SendQRCodeData user) {
        Call<Void> sendPresenceConfirmationCall = service.sendPresence(user);
        try {
            Response<Void> response = sendPresenceConfirmationCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error", e));
        }
    }
}
