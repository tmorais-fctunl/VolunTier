package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.Room.EventEntity;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteID;

public class AddRouteResult {
    @Nullable
    private final RouteID success;
    @Nullable
    private final Integer error;

    AddRouteResult(@Nullable RouteID success, @Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    public RouteID getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
