package pt.unl.fct.di.aldeia.apdc2021.ui.logout;

import androidx.annotation.Nullable;

public class LogoutResult {
    @Nullable
    private Integer success;
    @Nullable
    private Integer error;

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


