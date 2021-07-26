package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.Room.EventEntity;

public class AddEventResult {
    @Nullable
    private final EventEntity success;
    @Nullable
    private final Integer error;

    AddEventResult(@Nullable EventEntity success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public EventEntity getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
