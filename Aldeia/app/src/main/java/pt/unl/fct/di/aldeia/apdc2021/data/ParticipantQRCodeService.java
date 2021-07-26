package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.QRCode;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ParticipantQRCodeService {
    @POST("rest/event/presenceCode")
    Call<QRCode> getParticipantQRCode(@Body GetEventData event);
}
