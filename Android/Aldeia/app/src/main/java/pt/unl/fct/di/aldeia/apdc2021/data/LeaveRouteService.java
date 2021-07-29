package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.LeaveRouteData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LeaveRouteService {
    @POST("rest/route/removeParticipant")
    Call<Void> participateInRoute(@Body LeaveRouteData route);
}
