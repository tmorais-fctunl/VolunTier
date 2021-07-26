package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.SendPresenceConfirmationService;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EarnedAmmount;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SendQRCodeData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendLeaveConfirmationDataSource {
    private final SendLeaveConfirmationService service;

    public SendLeaveConfirmationDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(SendLeaveConfirmationService.class);
    }

    public Result<EarnedAmmount> sendLeaveConfirmation(SendQRCodeData data) {
        Call<EarnedAmmount> leaveEventConfirmationCall = service.sendLeave(data);
        try {
            Response<EarnedAmmount> response = leaveEventConfirmationCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error", e));
        }
    }
}
