package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.ReceivedSearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsCategoryReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchUserData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchEventCategoryDataSource {
    private final SearchEventCategoryService service;

    public SearchEventCategoryDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(SearchEventCategoryService.class);
    }

    public Result<SearchEventsCategoryReply> searchEventCategory(RankingRequestData data, String category) {
        Call<SearchEventsCategoryReply> searchEventCategoryCall =service.searchEventCategory(data, category) ;
        try {
            Response<SearchEventsCategoryReply> response = searchEventCategoryCall.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error getting events", e));
        }
    }
}