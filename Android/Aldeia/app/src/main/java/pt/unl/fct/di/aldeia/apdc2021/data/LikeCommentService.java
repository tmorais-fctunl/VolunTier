package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LikeCommentData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LikeCommentService {
    @POST("rest/likeComment")
    Call<Void> likeComment(@Body LikeCommentData data);
}
