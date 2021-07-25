package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetChat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CommentRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.EditCommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetChatCommentUnit;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LikeCommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat.AddCommentResult;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat.CommentDialog;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat.EventChatAdapter;

public class RouteChatActivity extends AppCompatActivity implements CommentDialog.CommentDialogListener{

    private RecyclerView recyclerView;
    private RouteChatViewModel viewModel;
    private RouteChatActivity mActivity;
    private UserLocalStore storage;
    private String routeID;
    private GetRouteChatReply comments;
    private RouteChatAdapter adapter;
    private UserAuthenticated user;
    private String userStatus;
    private TextView noComments;
    private String commentToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routeID=getIntent().getStringExtra("routeID");
        userStatus=getIntent().getStringExtra("status");
        storage= new UserLocalStore(this);
        user=storage.getLoggedInUser();
        setContentView(R.layout.activity_route_chat);
        recyclerView=findViewById(R.id.routeParticipantsRecyclerView);
        viewModel=new ViewModelProvider(this, new RouteChatViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(RouteChatViewModel.class);
        mActivity=this;
        Context context=this;
        UserAuthenticated user=storage.getLoggedInUser();
        viewModel.getChat(new GetRouteChatData(user.getTokenID(),user.getEmail(),routeID,true,0));
        comments=null;
        Button loadMoreComments=findViewById(R.id.routegetChatLoadMore);
        noComments=findViewById(R.id.routegetChatNoComments);
        adapter=null;
        UserFullData fullData=storage.getUserFullData();
        ImageButton addComment= findViewById(R.id.routegetChatAddComment);

        viewModel.getChatResult().observe(this, new Observer<RouteChatResult>() {
            @Override
            public void onChanged(RouteChatResult routeChatResult) {
                if(routeChatResult==null){
                    return;
                }
                if(routeChatResult.getError()!=null){
                    Toast.makeText(mActivity, routeChatResult.getError() ,Toast.LENGTH_SHORT).show();
                }
                if(routeChatResult.getSuccess()!=null){
                    if(comments==null){
                        comments =routeChatResult.getSuccess();
                        if(comments.getComments().size()==0){
                            noComments.setVisibility(View.VISIBLE);
                        }
                    }else{
                        List<GetChatCommentUnit> original=comments.getComments();
                        List<GetChatCommentUnit> loadedComments=routeChatResult.getSuccess().getComments();
                        for(int i =0;i<loadedComments.size();i++){
                            original.add(loadedComments.get(i));
                        }
                        comments=new GetRouteChatReply(original,routeChatResult.getSuccess().getResults(),routeChatResult.getSuccess().getCursor());
                    }
                    adapter = new RouteChatAdapter(context,comments.getComments(),userStatus,fullData.getUsername());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                }
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDialog dialog = new CommentDialog();
                dialog.show(mActivity.getSupportFragmentManager(),"");
            }
        });

        viewModel.getAddCommentResult().observe(this, new Observer<RouteAddCommentResult>() {
            @Override
            public void onChanged(RouteAddCommentResult routeAddCommentResult) {
                if(routeAddCommentResult==null){
                    return;
                }
                if(routeAddCommentResult.getError()!=null){
                    if(userStatus.equals("NON_PARTICIPANT")||userStatus.equals("PENDING")){
                        Toast.makeText(mActivity, R.string.invalid_chat_action_status ,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mActivity, routeAddCommentResult.getError() ,Toast.LENGTH_SHORT).show();
                    }
                }
                if(routeAddCommentResult.getSuccess()!=null){
                    List<GetChatCommentUnit> original=comments.getComments();
                    original.add(0,new GetChatCommentUnit(user.getEmail(), fullData.getUsername(),commentToAdd,"Just Now",routeAddCommentResult.getSuccess().getComment_id(),0,false ));
                    comments=new GetRouteChatReply(original,comments.getResults(),comments.getCursor());
                    adapter = new RouteChatAdapter(context,comments.getComments(),userStatus,fullData.getUsername());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                }
            }
        });

        loadMoreComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comments.getResults().equals("MORE_RESULTS_AFTER_LIMIT")){
                    viewModel.getChat(new GetRouteChatData(user.getTokenID(),user.getEmail(),routeID,true,comments.getCursor()));
                }else{
                    Toast.makeText(mActivity, R.string.event_no_more_comments,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onDialogPositiveClick(String comment) {
        commentToAdd=comment;
        viewModel.addCommentCommand(new CommentRouteData(user.getEmail(),user.getTokenID(),routeID,comment));
        noComments.setVisibility(View.INVISIBLE);
    }
}