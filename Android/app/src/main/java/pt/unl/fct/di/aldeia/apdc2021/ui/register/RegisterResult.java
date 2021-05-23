package pt.unl.fct.di.aldeia.apdc2021.ui.register;


import androidx.annotation.Nullable;


/**
 * Registration result : success (nothing) or error message.
 */
class RegisterResult {
    @Nullable
    private Integer error;
    private Integer success;

    RegisterResult(@Nullable Integer success ,@Nullable Integer error) {
        this.success=success;
        this.error = error;
    }

    @Nullable
    Integer getError() {
        return error;
    }

    @Nullable
    Integer getSuccess() {
        return success;
    }
}
