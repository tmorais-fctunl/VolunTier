package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.TokenCredentials;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ValidateTokenDataSource {
    private final ValidateTokenService service;

    public ValidateTokenDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(ValidateTokenService.class);
    }

    public Result<Void> validate(String email, String token) {
        Call<Void> tokenValidationCall = service.validateToken(new TokenCredentials(email, token));
        try {
            Response<Void> response = tokenValidationCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

}
