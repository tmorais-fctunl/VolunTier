package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoReply;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UpdatePhotoService {
    @POST("rest/update/picture")
    Call<UpdatePhotoReply> updateFoto(@Body UpdatePhotoData data);

}
