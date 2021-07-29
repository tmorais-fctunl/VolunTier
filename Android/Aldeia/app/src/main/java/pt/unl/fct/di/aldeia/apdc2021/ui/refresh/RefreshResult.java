package pt.unl.fct.di.aldeia.apdc2021.ui.refresh;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

public class RefreshResult {
    @Nullable
    private UserAuthenticated success;
    @Nullable
    private Integer error;

    RefreshResult(@Nullable Integer error) {
        this.error = error;
    }

    RefreshResult(@Nullable UserAuthenticated success) {
        this.success = success;
    }

    @Nullable
    UserAuthenticated getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
