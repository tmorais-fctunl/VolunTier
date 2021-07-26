package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadPhotoGCDataSource {

    private final UploadPhotoGCService service;

    public UploadPhotoGCDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(UploadPhotoGCService.class);
    }





    public Result<Void> uploadPhotoGC(byte[] photo,String url) {
        Call<Void> updatePhoto = service.updatePhotoGC(photo,url);
        try {
            Response<Void> response = updatePhoto.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error updating photo", e));
        }
    }
}