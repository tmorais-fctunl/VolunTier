package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PointsRankService {
    @POST("rest/totalCurrencyRank")
    Call<RankingReceivedData> lookUpPointsRanking(@Body RankingRequestData data);
}