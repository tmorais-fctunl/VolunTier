package pt.unl.fct.di.aldeia.apdc2021.data;

import java.io.IOException;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PointsRankDataSource {

    private final PointsRankService service;

    public PointsRankDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://voluntier-317915.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(PointsRankService.class);
    }

    public Result<RankingReceivedData> lookUpPointsRanking(RankingRequestData data) {
        Call<RankingReceivedData> lookUpRanking = service.lookUpPointsRanking(data);
        try {
            Response<RankingReceivedData> response = lookUpRanking.execute();
            if(response.isSuccessful()) {
                return new Result.Success<>(response.body());
            }
            return new Result.Error(new Exception(response.errorBody().toString()));
        } catch (IOException e) {
            return new Result.Error(new IOException("Error looking up Ranking", e));
        }
    }

}