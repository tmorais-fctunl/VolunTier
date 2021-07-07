package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;

public class LogoutResult {
    @Nullable
    private final Integer success;
    @Nullable
    private final Integer error;

    LogoutResult(@Nullable Integer success,@Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    Integer getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}


