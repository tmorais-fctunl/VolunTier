package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.QRCode;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserChangePassword;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParticipantQRCodeDataSource {
    private final ParticipantQRCodeService service;

    public ParticipantQRCodeDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(ParticipantQRCodeService.class);
    }

    public Result<QRCode> getQRCode(GetEventData data) {
        Call<QRCode> userUpdateCall = service.getParticipantQRCode(data);
        try {
            Response<QRCode> response = userUpdateCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error getting QRCode", e));
        }
    }
}
