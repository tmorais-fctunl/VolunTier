package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.TokenCredentials;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LookUpDataSource {
    private final LookUpService service;

    public LookUpDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(LookUpService.class);
    }

    public Result<UserFullData> lookUp(String email, String token) {
        Call<UserFullData> lookUpCall = service.lookUpUser(new TokenCredentials(email, token));
        try {
            Response<UserFullData> response = lookUpCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error getting your data", e));
        }
    }

}