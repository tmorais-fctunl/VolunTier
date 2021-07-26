package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.DonationInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DonateService {
    @POST("rest/causes/donate")
    Call<Void> donate(@Body DonationInfo data);
}