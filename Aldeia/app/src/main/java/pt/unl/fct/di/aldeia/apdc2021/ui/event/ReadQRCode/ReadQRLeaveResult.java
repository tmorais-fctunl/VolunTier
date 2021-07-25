package pt.unl.fct.di.aldeia.apdc2021.ui.event.ReadQRCode;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EarnedAmmount;

public class ReadQRLeaveResult {
    @Nullable
    private final EarnedAmmount success;
    @Nullable
    private final Integer error;

    public ReadQRLeaveResult(@Nullable EarnedAmmount success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public EarnedAmmount getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
