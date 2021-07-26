package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetChat;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import pt.unl.fct.di.aldeia.apdc2021.data.AddCommentDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.AddCommentRouteDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.DeleteCommentDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.EditCommentDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventChatDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetEventChatRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteChatDataSource;
import pt.unl.fct.di.aldeia.apdc2021.data.GetRouteChatRepository;
import pt.unl.fct.di.aldeia.apdc2021.data.LikeCommentDataSource;


public class RouteChatViewModelFactory implements ViewModelProvider.Factory {
    private final Executor executor;

    public RouteChatViewModelFactory (Executor executor) {
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RouteChatViewModel.class)) {
            return (T) new RouteChatViewModel(GetRouteChatRepository.getInstance(new GetRouteChatDataSource(), new AddCommentRouteDataSource()), executor);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}