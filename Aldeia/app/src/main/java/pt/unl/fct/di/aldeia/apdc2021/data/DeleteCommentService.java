package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.LikeCommentData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeleteCommentService {
    @POST("rest/deleteComment")
    Call<Void> deleteComment(@Body LikeCommentData data);
}
