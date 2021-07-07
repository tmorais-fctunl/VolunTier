package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserChangePassword;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordDataSource {
    private final ChangeProfileService service;

    public ChangePasswordDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(ChangeProfileService.class);
    }

    public Result<Void> changeProfilePassword(String email, String token, String oldPassword,String password,String passwordConfirmation) {
        UserChangePassword user= new UserChangePassword(email,token,oldPassword,password,passwordConfirmation);
        Call<Void> userUpdateCall = service.changePassword(user);
        try {
            Response<Void> response = userUpdateCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error", e));
        }
    }
}
