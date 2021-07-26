package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteID;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddRouteService {
    @POST("rest/route/create")
    Call<RouteID> addRoute(@Body CreateRouteData event);
}
