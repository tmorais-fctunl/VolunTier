package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventUpdateData;

public class UpdateEventResult {
    @Nullable
    private final EventUpdateData success;
    @Nullable
    private final Integer error;

    UpdateEventResult(@Nullable EventUpdateData success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    EventUpdateData getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
