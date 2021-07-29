package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventID;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LikeCommentData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeleteCommentDataSource {
    private final DeleteCommentService service;

    public DeleteCommentDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(DeleteCommentService.class);
    }

    public Result<Void> deleteComment(LikeCommentData data) {
        Call<Void> addEventCall = service.deleteComment(data);
        try {
            Response<Void> response = addEventCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error deleting comment", e));
        }
    }

}
