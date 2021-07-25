package pt.unl.fct.di.aldeia.apdc2021.ui.leaderboard;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;

public class RankRequestResult {
    @Nullable
    private final RankingReceivedData success;
    @Nullable
    private final Integer error;

    public RankRequestResult(@Nullable RankingReceivedData success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public RankingReceivedData getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}