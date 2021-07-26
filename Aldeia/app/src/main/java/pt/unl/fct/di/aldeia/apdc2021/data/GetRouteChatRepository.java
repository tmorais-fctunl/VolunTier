package pt.unl.fct.di.aldeia.apdc2021.data;

import pt.unl.fct.di.aldeia.apdc2021.data.model.AddCommentReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteChatReply;

public class GetRouteChatRepository {
    private static volatile GetRouteChatRepository instance;

    private final GetRouteChatDataSource getRouteChatDataSource;
    private final AddCommentRouteDataSource addCommentRouteDataSource;

    // private constructor : singleton access
    private GetRouteChatRepository(GetRouteChatDataSource dataSource, AddCommentRouteDataSource addCommentRouteDataSource) {
        this.getRouteChatDataSource = dataSource;
        this.addCommentRouteDataSource = addCommentRouteDataSource;
    }

    public static GetRouteChatRepository getInstance(GetRouteChatDataSource getRouteChatDataSource, AddCommentRouteDataSource addCommentRouteDataSource) {
        if (instance == null) {
            instance = new GetRouteChatRepository(getRouteChatDataSource, addCommentRouteDataSource);
        }
        return instance;
    }

    public Result<GetRouteChatReply> getChat(GetRouteChatData data) {
        return getRouteChatDataSource.getChat(data);
    }

    public Result<AddCommentReply> addComment(CommentRouteData data) {
        return addCommentRouteDataSource.addCommentRoute(data);
    }

}
