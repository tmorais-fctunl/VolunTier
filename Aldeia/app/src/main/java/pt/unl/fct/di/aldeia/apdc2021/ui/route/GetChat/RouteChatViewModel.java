package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetChat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventChatRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteChatRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.AddCommentReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EditCommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LikeCommentData;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat.AddCommentResult;

public class RouteChatViewModel extends ViewModel {
    private final GetRouteChatRepository getRouteChatRepository;
    private final MutableLiveData<RouteChatResult> getChatResult = new MutableLiveData<>();
    private final MutableLiveData<RouteAddCommentResult> addCommentResult= new MutableLiveData<>();

    private final Executor executor;

    RouteChatViewModel(GetRouteChatRepository getRouteChatRepository, Executor executor) {
        this.executor = executor;
        this.getRouteChatRepository = getRouteChatRepository;
    }

    LiveData<RouteChatResult> getChatResult() {
        return getChatResult;
    }

    public void getChat(GetRouteChatData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<GetRouteChatReply> result = getRouteChatRepository.getChat(data);
                if (result instanceof Result.Success) {
                    GetRouteChatReply data = ((Result.Success<GetRouteChatReply>) result).getData();
                    getChatResult.postValue(new RouteChatResult(data,null));
                } else {
                    getChatResult.postValue(new RouteChatResult(null,R.string.event_chat_failed));
                }
            }
        });
    }

    LiveData<RouteAddCommentResult> getAddCommentResult(){return addCommentResult;}

    public void addCommentCommand(CommentRouteData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<AddCommentReply> result = getRouteChatRepository.addComment(data);
                if (result instanceof Result.Success) {
                    addCommentResult.postValue(new RouteAddCommentResult(((Result.Success<AddCommentReply>) result).getData(),null));
                } else {
                    addCommentResult.postValue(new RouteAddCommentResult(null,R.string.event_comment_failed));
                }
            }
        });
    }
}