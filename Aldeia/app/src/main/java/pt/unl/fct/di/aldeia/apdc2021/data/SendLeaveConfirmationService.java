package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EarnedAmmount;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SendQRCodeData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SendLeaveConfirmationService {
    @POST("rest/event/confirmLeave")
    Call<EarnedAmmount> sendLeave(@Body SendQRCodeData data);
}
