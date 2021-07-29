package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.AddCommentDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.DeleteCommentDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.EditCommentDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventChatDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventChatRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.LikeCommentDataSource;


public class EventChatViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public EventChatViewModelFactory (Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EventChatViewModel.class)) {
            return (T) new EventChatViewModel(GetEventChatRepository.getInstance(new GetEventChatDataSource(), new LikeCommentDataSource(),new AddCommentDataSource(),new DeleteCommentDataSource(),new EditCommentDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
