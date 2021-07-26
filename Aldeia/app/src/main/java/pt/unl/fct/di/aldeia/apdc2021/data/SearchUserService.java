package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.ReceivedSearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchUserData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SearchUserService {
    @POST("rest/search")
    Call<SearchUserData> searchUser(@Body ReceivedSearchUserData data, @Query("q") String username);
}