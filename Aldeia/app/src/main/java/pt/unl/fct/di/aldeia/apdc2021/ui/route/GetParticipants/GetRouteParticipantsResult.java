package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetParticipants;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;

public class GetRouteParticipantsResult {
    @Nullable
    private final GetParticipantsReply success;
    @Nullable
    private final Integer error;

    GetRouteParticipantsResult(@Nullable GetParticipantsReply success,@Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    GetParticipantsReply getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}