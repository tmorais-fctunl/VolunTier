package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;

public class SearchEventsResult {

    private SearchEventsReply success;
    private Integer error;

    public SearchEventsResult(@Nullable SearchEventsReply success,@Nullable Integer error){
        this.success=success;
        this.error=error;
    }

    public SearchEventsReply getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
