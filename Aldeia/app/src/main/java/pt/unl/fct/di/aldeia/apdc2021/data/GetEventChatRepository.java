package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AddCommentReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EditCommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LikeCommentData;

public class GetEventChatRepository {
    private static volatile GetEventChatRepository instance;

    private final GetEventChatDataSource getEventChatDataSource;
    private final LikeCommentDataSource likeCommentDataSource;
    private final AddCommentDataSource addCommentDataSource;
    private final DeleteCommentDataSource deleteCommentDataSource;
    private final EditCommentDataSource editCommentDataSource;

    // private constructor : singleton access
    private GetEventChatRepository(GetEventChatDataSource dataSource, LikeCommentDataSource likeCommentDataSource,
                                   AddCommentDataSource addCommentDataSource,DeleteCommentDataSource deleteCommentDataSource,EditCommentDataSource editCommentDataSource) {
        this.getEventChatDataSource = dataSource;
        this.likeCommentDataSource=likeCommentDataSource;
        this.addCommentDataSource=addCommentDataSource;
        this.deleteCommentDataSource=deleteCommentDataSource;
        this.editCommentDataSource=editCommentDataSource;
    }

    public static GetEventChatRepository getInstance(GetEventChatDataSource getEventChatDataSource, LikeCommentDataSource likeCommentDataSource
            ,AddCommentDataSource addCommentDataSource,DeleteCommentDataSource deleteCommentDataSource, EditCommentDataSource editCommentDataSource) {
        if (instance == null) {
            instance = new GetEventChatRepository(getEventChatDataSource,likeCommentDataSource,addCommentDataSource,deleteCommentDataSource,editCommentDataSource);
        }
        return instance;
    }

    public Result<GetEventChatReply> getChat(GetEventChatData data) {
        return getEventChatDataSource.getChat(data);
    }

    public Result<Void> likeComment(LikeCommentData data){
        return likeCommentDataSource.likeComment(data);
    }

    public Result<AddCommentReply> addComment(CommentData data){
        return addCommentDataSource.addComment(data);
    }

    public Result<Void> deleteComment(LikeCommentData data){
        return deleteCommentDataSource.deleteComment(data);
    }

    public Result<Void> editComment(EditCommentData data){
        return editCommentDataSource.editComment(data);
    }
}
