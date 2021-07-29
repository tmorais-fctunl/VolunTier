package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventChatRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.Result;
import pt.unl.fct.di.aldeia.apdc2021.data.model.AddCommentReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EditCommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LikeCommentData;

public class EventChatViewModel extends ViewModel {
    private final GetEventChatRepository getEventChatRepository;
    private final MutableLiveData<EventChatResult> getChatResult = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> likeCommentResult = new MutableLiveData<>();
    private final MutableLiveData<AddCommentResult> addCommentResult= new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> deleteCommentResult = new MutableLiveData<>();
    private final MutableLiveData<DefaultResult> editCommentResult = new MutableLiveData<>();

    private final Executor executor;

    EventChatViewModel(GetEventChatRepository getEventChatRepository, Executor executor) {
        this.executor = executor;
        this.getEventChatRepository = getEventChatRepository;
    }

    LiveData<EventChatResult> getChatResult() {
        return getChatResult;
    }

    LiveData<DefaultResult> getLikeCommentResult() {
        return likeCommentResult;
    }

    LiveData<AddCommentResult> getAddCommentResult(){return addCommentResult;}

    LiveData<DefaultResult> getDeleteCommentResult() {
        return deleteCommentResult;
    }

    LiveData<DefaultResult> getEditCommentResult() {
        return editCommentResult;
    }

    public void getChat(GetEventChatData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<GetEventChatReply> result = getEventChatRepository.getChat(data);
                if (result instanceof Result.Success) {
                    GetEventChatReply data = ((Result.Success<GetEventChatReply>) result).getData();
                    getChatResult.postValue(new EventChatResult(data,null));
                } else {
                    getChatResult.postValue(new EventChatResult(null,R.string.event_chat_failed));
                }
            }
        });
    }

    public void likeCommand(LikeCommentData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = getEventChatRepository.likeComment(data);
                if (result instanceof Result.Success) {
                    likeCommentResult.postValue(new DefaultResult(R.string.event_participation_succeeded,null));
                } else {
                    likeCommentResult.postValue(new DefaultResult(null,R.string.event_participation_failed));
                }
            }
        });
    }

    public void addCommentCommand(CommentData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<AddCommentReply> result = getEventChatRepository.addComment(data);
                if (result instanceof Result.Success) {
                    addCommentResult.postValue(new AddCommentResult(((Result.Success<AddCommentReply>) result).getData(),null));
                } else {
                    addCommentResult.postValue(new AddCommentResult(null,R.string.event_comment_failed));
                }
            }
        });
    }

    public void deleteCommentCommand(LikeCommentData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = getEventChatRepository.deleteComment(data);
                if (result instanceof Result.Success) {
                    deleteCommentResult.postValue(new DefaultResult(R.string.delete_comment_succeeded ,null));
                } else {
                    deleteCommentResult.postValue(new DefaultResult(null,R.string.delete_comment_failed));
                }
            }
        });
    }

    public void editCommentCommand(EditCommentData data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Void> result = getEventChatRepository.editComment(data);
                if (result instanceof Result.Success) {
                    editCommentResult.postValue(new DefaultResult(R.string.edit_comment_succeeded ,null));
                } else {
                    editCommentResult.postValue(new DefaultResult(null,R.string.edit_comment_failed));
                }
            }
        });
    }
}
