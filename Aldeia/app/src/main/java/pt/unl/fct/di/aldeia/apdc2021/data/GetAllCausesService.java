package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AllCausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.TokenCredentials;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetAllCausesService {
    @POST("rest/causes/get/all")
    Call<AllCausesData> getAllCauses(@Body TokenCredentials data);
}