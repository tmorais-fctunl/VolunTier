package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;

public class PointsRankRepository {
    private static volatile PointsRankRepository instance;

    private final PointsRankDataSource dataSource;

    // private constructor : singleton access
    private PointsRankRepository(PointsRankDataSource dataSource) {

        this.dataSource = dataSource;

    }

    public static PointsRankRepository getInstance(PointsRankDataSource dataSource) {
        if (instance == null) {
            instance = new PointsRankRepository(dataSource);
        }
        return instance;
    }



    public Result<RankingReceivedData> lookUpPointsRanking(RankingRequestData data) {
        Result<RankingReceivedData> result = dataSource.lookUpPointsRanking(data);
        return result;
    }
}