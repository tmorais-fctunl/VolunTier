package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteRatingData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserCredentials;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserUpdateData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RateRouteDataSource {
    private final RateRouteService service;

    public RateRouteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(RateRouteService.class);
    }

    public Result<Void> rateRoute(RouteRatingData user) {
        Call<Void> userUpdateCall = service.rateRoute(user);
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