package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.ReceivedSearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SendQRCodeData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SendPresenceConfirmationService {
    @POST("rest/event/confirmPresence")
    Call<Void> sendPresence(@Body SendQRCodeData data);
}

