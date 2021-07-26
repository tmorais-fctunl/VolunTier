package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParticipateRouteDataSource {
    private final ParticipateRouteService service;

    public ParticipateRouteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
        this.service = retrofit.create(ParticipateRouteService.class);
    }

    public Result<Void> participateRoute(GetRouteData route) {
        Call<Void> logoutCall = service.participateInRoute(route);
        try {
            Response<Void> response = logoutCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error participating in route", e));
        }
    }
}