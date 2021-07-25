package pt.unl.fct.di.aldeia.apdc2021.ui.event.QRCode;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.QRCode;

public class QRCodeResult {
    @Nullable
    private final QRCode success;
    @Nullable
    private final Integer error;

    public QRCodeResult(@Nullable QRCode success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public QRCode getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
