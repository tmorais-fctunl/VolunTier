package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AllCausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.TokenCredentials;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetAllCausesDataSource {
    private final GetAllCausesService service;

    public GetAllCausesDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(GetAllCausesService.class);
    }

    public Result<AllCausesData> getAllCauses(TokenCredentials data) {
        Call<AllCausesData> getChatCall = service.getAllCauses(data);
        try {
            Response<AllCausesData> response = getChatCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error getting causes", e));
        }
    }
}