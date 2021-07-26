package pt.unl.fct.di.aldeia.apdc2021.data;

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
public class LoginDataSource {

    private final LoginService service;

    public LoginDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(LoginService.class);
    }

    public Result<UserAuthenticated> login(String email, String password) {
        //String encPassword= DigestUtils.sha512Hex(password);
        Call<UserAuthenticated> userAuthenticationCall = service.authenticateUser(new UserCredentials(email, password));
        try {
            Response<UserAuthenticated> response = userAuthenticationCall.execute();
            if(response.isSuccessful()) {
                UserAuthenticated ua = response.body();
                return new Result.Success<>(ua);
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
}