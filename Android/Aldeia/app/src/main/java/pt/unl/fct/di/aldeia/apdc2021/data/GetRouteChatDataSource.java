package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteChatReply;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetRouteChatDataSource {
    private final GetRouteChatService service;

    public GetRouteChatDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(GetRouteChatService.class);
    }

    public Result<GetRouteChatReply> getChat(GetRouteChatData data) {
        Call<GetRouteChatReply> getChatCall = service.getChat(data);
        try {
            Response<GetRouteChatReply> response = getChatCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error getting chat", e));
        }
    }
}