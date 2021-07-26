package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;


import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchRoutesReply;

public class SearchRoutesResult {
    private SearchRoutesReply success;
    private Integer error;

    public SearchRoutesResult(@Nullable SearchRoutesReply success, @Nullable Integer error){
        this.success=success;
        this.error=error;
    }

    public SearchRoutesReply getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
