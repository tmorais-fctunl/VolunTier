package pt.unl.fct.di.aldeia.apdc2021.ui.login;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;

public class LookUpResult {
    @Nullable
    private final UserFullData success;
    @Nullable
    private final Integer error;

    LookUpResult(@Nullable UserFullData success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    UserFullData getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}


