package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteRatingData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RateRouteService {
    @POST("rest/route/rate")
    Call<Void> rateRoute(@Body RouteRatingData data);
}