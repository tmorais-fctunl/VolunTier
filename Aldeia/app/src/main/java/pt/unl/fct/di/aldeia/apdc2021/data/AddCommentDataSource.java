package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AddCommentReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventID;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddCommentDataSource {
    private final AddCommentService service;

    public AddCommentDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(AddCommentService.class);
    }

    public Result<AddCommentReply> addComment(CommentData event) {
        Call<AddCommentReply> addEventCall = service.postComment(event);
        try {
            Response<AddCommentReply> response = addEventCall.execute();
            if(response.isSuccessful()) {

                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error adding comment", e));
        }
    }
}
