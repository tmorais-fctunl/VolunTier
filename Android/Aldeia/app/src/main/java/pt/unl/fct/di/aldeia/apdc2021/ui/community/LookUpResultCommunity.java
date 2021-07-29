package pt.unl.fct.di.aldeia.apdc2021.ui.community;

import androidx.annotation.Nullable;

import java.io.Serializable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;

public class LookUpResultCommunity {
    @Nullable
    private final UserFullData success;
    @Nullable
    private final Integer error;

    public LookUpResultCommunity(@Nullable UserFullData success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public UserFullData getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}