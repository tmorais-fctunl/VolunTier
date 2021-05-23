package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.TokenCredentials;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RefreshTokenDataSource {

        private RefreshTokenService service;

        public RefreshTokenDataSource() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://voluntier-312115.ew.r.appspot.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            this.service = retrofit.create(RefreshTokenService.class);
        }

        public Result<UserAuthenticated> refreshToken(String username, String refreshToken) {
            Call<UserAuthenticated> userRefreshCall = service.refreshUser(new TokenCredentials(username, refreshToken));
            try {
                Response<UserAuthenticated> response = userRefreshCall.execute();
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
