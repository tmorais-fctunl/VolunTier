package pt.unl.fct.di.aldeia.apdc2021.ui.route;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;

public class GetRouteResult {
    @Nullable
    private GetRouteReply success;
    @Nullable
    private Integer error;

    GetRouteResult(@Nullable GetRouteReply success,@Nullable Integer error) {
        this.success=success;
        this.error = error;
    }



    @Nullable
    GetRouteReply getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
