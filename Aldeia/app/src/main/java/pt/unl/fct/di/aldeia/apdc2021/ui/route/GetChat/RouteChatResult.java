package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetChat;

import androidx.annotation.Nullable;

import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteChatReply;

public class RouteChatResult {
    @Nullable
    private final GetRouteChatReply success;
    @Nullable
    private final Integer error;

    RouteChatResult(@Nullable GetRouteChatReply success,@Nullable Integer error) {
        this.error = error;
        this.success=success;
    }



    @Nullable
    GetRouteChatReply getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}