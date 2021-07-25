package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetParticipants;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;

public class GetEventParticipantsResult {
    @Nullable
    private final GetParticipantsReply success;
    @Nullable
    private final Integer error;

    GetEventParticipantsResult(@Nullable GetParticipantsReply success,@Nullable Integer error) {
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
