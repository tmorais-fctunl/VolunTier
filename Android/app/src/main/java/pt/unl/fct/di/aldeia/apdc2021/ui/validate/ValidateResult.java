package pt.unl.fct.di.aldeia.apdc2021.ui.validate;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

public class ValidateResult {
    @Nullable
    private Integer success;
    @Nullable
    private Integer error;

    ValidateResult(@Nullable Integer success,@Nullable Integer error) {
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

