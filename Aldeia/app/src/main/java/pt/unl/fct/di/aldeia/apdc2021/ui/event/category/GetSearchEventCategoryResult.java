package pt.unl.fct.di.aldeia.apdc2021.ui.event.category;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsCategoryReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

public class GetSearchEventCategoryResult {

    @Nullable
    private SearchEventsCategoryReply success;
    @Nullable
    private Integer error;

    public GetSearchEventCategoryResult(@Nullable SearchEventsCategoryReply success , @Nullable Integer error) {
        this.success=success;
        this.error = error;
    }



    @Nullable
    public SearchEventsCategoryReply getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}