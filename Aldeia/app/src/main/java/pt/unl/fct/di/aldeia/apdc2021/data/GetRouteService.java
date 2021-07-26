package pt.unl.fct.di.aldeia.apdc2021.data;


import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteReply;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetRouteService {
    @POST("rest/route/data")
    Call<GetRouteReply> getRoute(@Body GetRouteData event);
}
