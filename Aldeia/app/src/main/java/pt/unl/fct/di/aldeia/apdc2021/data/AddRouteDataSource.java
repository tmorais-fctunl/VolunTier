package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AddCommentReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteID;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddRouteDataSource {
    private final AddRouteService service;

    public AddRouteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(AddRouteService.class);
    }

    public Result<RouteID> addRoute(CreateRouteData route) {
        Call<RouteID> routeCreationCall = service.addRoute(route);
        try {
            Response<RouteID> response = routeCreationCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            if (response.code() == 429) {
                return new  Result.Error(new Exception("429"));
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error creating route", e));
        }
    }
}
