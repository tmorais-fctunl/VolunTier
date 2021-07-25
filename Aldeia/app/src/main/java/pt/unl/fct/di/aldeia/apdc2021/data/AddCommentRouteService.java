package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AddCommentReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CreateEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EventID;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddCommentRouteService {
    @POST("rest/postComment")
    Call<AddCommentReply> postComment(@Body CommentRouteData data);
}