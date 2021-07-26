package pt.unl.fct.di.aldeia.apdc2021.data;


import java.io.IOException;
import org.apache.commons.codec.digest.DigestUtils;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserRegistration;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterDataSource {

    private final RegisterService service;

    public RegisterDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(RegisterService.class);
    }

    public Result<Void> register(String username, String email, String password) {
        //String encPassword= DigestUtils.sha256Hex(password);
        Call<Void> userRegistrationCall = service.registerUser(new UserRegistration(username, email, password));
        try {
            Response<Void> response = userRegistrationCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error registering", e));
        }
    }

}
