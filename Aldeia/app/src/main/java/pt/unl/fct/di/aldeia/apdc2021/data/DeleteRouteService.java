package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LikeCommentData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeleteRouteService {
    @POST("rest/route/remove")
    Call<Void> deleteRoute(@Body GetRouteData data);
}
