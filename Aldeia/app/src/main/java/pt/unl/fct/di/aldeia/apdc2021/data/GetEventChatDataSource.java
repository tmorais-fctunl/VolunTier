package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetEventChatDataSource {
    private final GetEventChatService service;

    public GetEventChatDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(GetEventChatService.class);
    }

    public Result<GetEventChatReply> getChat(GetEventChatData data) {
        Call<GetEventChatReply> getChatCall = service.getChat(data);
        try {
            Response<GetEventChatReply> response = getChatCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error getting chat", e));
        }
    }
}
