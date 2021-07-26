package pt.unl.fct.di.aldeia.apdc2021.ui.event;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.EventFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

public class GetEventResult {

    @Nullable
    private EventFullData success;
    @Nullable
    private Integer error;

    GetEventResult(@Nullable EventFullData success ,@Nullable Integer error) {
        this.success=success;
        this.error = error;
    }



    @Nullable
    EventFullData getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}

