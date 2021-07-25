package pt.unl.fct.di.aldeia.apdc2021.ui.loadingScreen;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;

public class LookUpResult {
    @Nullable
    private final UserFullData success;
    @Nullable
    private final Integer error;

    public LookUpResult(@Nullable UserFullData success, @Nullable Integer error) {
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


