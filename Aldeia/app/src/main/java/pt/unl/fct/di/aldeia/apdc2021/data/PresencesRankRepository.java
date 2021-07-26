package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;

public class PresencesRankRepository {
    private static volatile PresencesRankRepository instance;

    private final PresencesRankDataSource dataSource;

    // private constructor : singleton access
    private PresencesRankRepository(PresencesRankDataSource dataSource) {

        this.dataSource = dataSource;

    }

    public static PresencesRankRepository getInstance(PresencesRankDataSource dataSource) {
        if (instance == null) {
            instance = new PresencesRankRepository(dataSource);
        }
        return instance;
    }



    public Result<RankingReceivedData> lookUpRanking(RankingRequestData data) {
        Result<RankingReceivedData> result = dataSource.lookUpRanking(data);
        return result;
    }
}