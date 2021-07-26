package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;

public class EventChatResult {
    @Nullable
    private final GetEventChatReply success;
    @Nullable
    private final Integer error;

    EventChatResult(@Nullable GetEventChatReply success,@Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    GetEventChatReply getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
