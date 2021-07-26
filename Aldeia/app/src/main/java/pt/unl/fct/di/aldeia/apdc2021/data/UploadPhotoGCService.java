package pt.unl.fct.di.aldeia.apdc2021.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface UploadPhotoGCService {
    @PUT
    Call<Void> updatePhotoGC(@Body byte[] photo, @Url String url);

}