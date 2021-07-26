package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EditCommentData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EditCommentService {
    @POST("rest/updateComment")
    Call<Void> editComment(@Body EditCommentData data);
}
