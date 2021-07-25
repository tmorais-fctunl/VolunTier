package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ParticipateRouteService {
    @POST("rest/route/participate")
    Call<Void> participateInRoute(@Body GetRouteData route);
}