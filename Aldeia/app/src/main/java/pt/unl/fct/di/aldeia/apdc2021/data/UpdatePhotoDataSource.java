package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UpdatePhotoReply;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdatePhotoDataSource {
    private final UpdatePhotoService service;

    public UpdatePhotoDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(UpdatePhotoService.class);
    }

    public Result<UpdatePhotoReply> updateEvent(UpdatePhotoData data) {
        Call<UpdatePhotoReply> photoUpdateCall = service.updateFoto(data);
        try {
            Response<UpdatePhotoReply> response = photoUpdateCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error updating foto", e));
        }
    }
}
