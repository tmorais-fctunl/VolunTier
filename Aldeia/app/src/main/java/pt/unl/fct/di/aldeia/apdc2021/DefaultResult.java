package pt.unl.fct.di.aldeia.apdc2021;

import androidx.annotation.Nullable;

public class DefaultResult {
    @Nullable
    private final Integer success;
    @Nullable
    private final Integer error;

    public DefaultResult(@Nullable Integer success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public Integer getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
