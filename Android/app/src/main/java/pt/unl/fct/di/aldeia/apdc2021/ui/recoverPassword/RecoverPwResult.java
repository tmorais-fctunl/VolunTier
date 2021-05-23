package pt.unl.fct.di.aldeia.apdc2021.ui.recoverPassword;

import androidx.annotation.Nullable;

public class RecoverPwResult {
    @Nullable
    private Integer success;
    @Nullable
    private Integer error;

    RecoverPwResult(@Nullable Integer success, @Nullable Integer error) {
        this.success = success;
        this.error = error;
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
