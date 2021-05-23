package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RecoverPwCredentials;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserRegistration;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecoverPwDataSource {

    private RecoverPwService service;

    public RecoverPwDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-312115.ew.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(RecoverPwService.class);
    }

    public Result<Void> recoverPassword(String username, String email) {
        Call<Void> recoverPasswordCall = service.recoverPassword(new RecoverPwCredentials(username, email));
        try {
            Response<Void> response = recoverPasswordCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error registering", e));
        }
    }

}