package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventMarkerData;

public class AddEventResult {
    @Nullable
    private final EventMarkerData success;
    @Nullable
    private final Integer error;

    AddEventResult(@Nullable EventMarkerData success,@Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public EventMarkerData getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
