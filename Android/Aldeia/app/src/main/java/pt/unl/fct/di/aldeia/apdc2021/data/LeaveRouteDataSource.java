package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LeaveRouteData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LeaveRouteDataSource {
    private final LeaveRouteService service;

    public LeaveRouteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(LeaveRouteService.class);
    }

    public Result<Void> leaveRoute(LeaveRouteData route) {
        Call<Void> leaveRouteCall = service.participateInRoute(route);
        try {
            Response<Void> response = leaveRouteCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error leaving route", e));
        }
    }
}
