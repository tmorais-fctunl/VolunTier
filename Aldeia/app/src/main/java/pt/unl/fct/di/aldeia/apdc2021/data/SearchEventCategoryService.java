package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsCategoryReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SearchEventCategoryService {
    @POST("rest/event/searchByCategory")
    Call<SearchEventsCategoryReply> searchEventCategory(@Body RankingRequestData data, @Query("c") String category);
}