package pt.unl.fct.di.aldeia.apdc2021.ui.community;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingReceivedData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingRequestData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchUserData;

public class SearchUserResult {
    @Nullable
    private final SearchUserData success;
    @Nullable
    private final Integer error;

    public SearchUserResult(@Nullable SearchUserData success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public SearchUserData getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}