package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat;

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
import pt.unl.fct.di.aldeia.apdc2021.data.model.EditCommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetChatCommentUnit;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LikeCommentData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserFullData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;

public class EventChatActivity extends AppCompatActivity implements LikeClickEventInterface, CommentDialog.CommentDialogListener, EditCommentDialog.EditCommentDialogListener {

    private RecyclerView recyclerView;
    private EventChatViewModel viewModel;
    private EventChatActivity mActivity;
    private UserLocalStore storage;
    private String eventID;
    private GetEventChatReply comments;
    private EventChatAdapter adapter;
    private UserAuthenticated user;
    private String commentToAdd;
    private int deleteIndex;
    private String userStatus;
    private int editingIndex;
    private String editedComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventID=getIntent().getStringExtra("eventID");
        userStatus=getIntent().getStringExtra("status");
        storage= new UserLocalStore(this);
        user=storage.getLoggedInUser();
        setContentView(R.layout.activity_event_chat);
        recyclerView=findViewById(R.id.eventParticipantsRecyclerView);
        viewModel=new ViewModelProvider(this, new EventChatViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(EventChatViewModel.class);
        mActivity=this;
        Context context=this;
        UserAuthenticated user=storage.getLoggedInUser();
        viewModel.getChat(new GetEventChatData(user.getTokenID(),user.getEmail(),eventID,true,0));
        comments=null;
        ImageButton addComment= findViewById(R.id.getChatAddComment);
        Button loadMoreComments=findViewById(R.id.getChatLoadMore);
        TextView noComments=findViewById(R.id.getChatNoComments);
        adapter=null;
        commentToAdd=null;
        deleteIndex=-1;
        editingIndex=-1;
        editedComment =null;
        UserFullData fullData=storage.getUserFullData();
        viewModel.getChatResult().observe(this, new Observer<EventChatResult>() {
            @Override
            public void onChanged(EventChatResult eventChatResult) {
                if(eventChatResult==null){
                    return;
                }
                if(eventChatResult.getError()!=null){
                    Toast.makeText(mActivity, eventChatResult.getError() ,Toast.LENGTH_SHORT);
                }
                if(eventChatResult.getSuccess()!=null){
                    if(comments==null){
                        comments =eventChatResult.getSuccess();
                        if(comments.getComments().size()==0){
                            noComments.setVisibility(View.VISIBLE);
                        }
                    }else{
                        List<GetChatCommentUnit> original=comments.getComments();
                        List<GetChatCommentUnit> loadedComments=eventChatResult.getSuccess().getComments();
                        for(int i =0;i<loadedComments.size();i++){
                            original.add(loadedComments.get(i));
                        }
                        comments=new GetEventChatReply(original,eventChatResult.getSuccess().getResults(),eventChatResult.getSuccess().getCursor());
                    }
                    adapter = new EventChatAdapter(mActivity,context,comments.getComments(),userStatus,fullData.getUsername());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                }
            }
        });
        viewModel.getLikeCommentResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult likeCommentResult) {
                if(likeCommentResult==null){
                    return;
                }
                if(likeCommentResult.getError()!=null){
                    if(userStatus.equals("NON_PARTICIPANT")||userStatus.equals("PENDING")){
                        Toast.makeText(mActivity, R.string.invalid_chat_action_status ,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mActivity, likeCommentResult.getError() ,Toast.LENGTH_SHORT).show();
                    }
                }
                if(likeCommentResult.getSuccess()!=null){
                    Toast.makeText(mActivity, likeCommentResult.getSuccess() ,Toast.LENGTH_SHORT).show();

                }
            }
        });
        viewModel.getAddCommentResult().observe(this, new Observer<AddCommentResult>() {
            @Override
            public void onChanged(AddCommentResult addCommentResult) {
                if(addCommentResult==null){
                    return;
                }
                if(addCommentResult.getError()!=null){
                    if(userStatus.equals("NON_PARTICIPANT")||userStatus.equals("PENDING")){
                        Toast.makeText(mActivity, R.string.invalid_chat_action_status ,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mActivity, addCommentResult.getError() ,Toast.LENGTH_SHORT).show();
                    }
                }
                if(addCommentResult.getSuccess()!=null){
                    List<GetChatCommentUnit> original=comments.getComments();
                    original.add(0,new GetChatCommentUnit(user.getEmail(), fullData.getUsername(),commentToAdd,"Just Now",addCommentResult.getSuccess().getComment_id(),0,false ));
                    comments=new GetEventChatReply(original,comments.getResults(),comments.getCursor());
                    adapter = new EventChatAdapter(mActivity,context,comments.getComments(),userStatus,fullData.getUsername());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                }
            }
        });

        viewModel.getDeleteCommentResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult defaultResult) {
                if(defaultResult==null){
                    return;
                }
                if(defaultResult.getError()!=null){
                    Toast.makeText(mActivity, defaultResult.getError() ,Toast.LENGTH_SHORT).show();
                }
                if(defaultResult.getSuccess()!=null){
                    List<GetChatCommentUnit> original=comments.getComments();
                    original.remove(deleteIndex);
                    comments=new GetEventChatReply(original,comments.getResults(),comments.getCursor());
                    adapter = new EventChatAdapter(mActivity,context,comments.getComments(),userStatus,fullData.getUsername());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                    Toast.makeText(mActivity, defaultResult.getSuccess() ,Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getEditCommentResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult defaultResult) {
                if(defaultResult==null){
                    return;
                }
                if(defaultResult.getError()!=null){
                    Toast.makeText(mActivity, defaultResult.getError() ,Toast.LENGTH_SHORT).show();
                }
                if(defaultResult.getSuccess()!=null){
                    List<GetChatCommentUnit> original=comments.getComments();
                    GetChatCommentUnit previous= original.remove(editingIndex);
                    original.add(editingIndex,new GetChatCommentUnit(user.getEmail(), fullData.getUsername(),editedComment, previous.getTimestamp(), previous.getComment_id(),previous.getLikes(),previous.isLike_status()));
                    comments=new GetEventChatReply(original,comments.getResults(),comments.getCursor());
                    adapter = new EventChatAdapter(mActivity,context,comments.getComments(),userStatus,fullData.getUsername());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                    Toast.makeText(mActivity, defaultResult.getSuccess() ,Toast.LENGTH_SHORT).show();
                }
            }
        });
        loadMoreComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comments.getResults().equals("MORE_RESULTS_AFTER_LIMIT")){
                    viewModel.getChat(new GetEventChatData(user.getTokenID(),user.getEmail(),eventID,true,comments.getCursor()));
                }else{
                    Toast.makeText(mActivity, R.string.event_no_more_comments,Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onLikeButtonClickListener(int position) {
        viewModel.likeCommand(new LikeCommentData(user.getEmail(),user.getTokenID(),eventID,comments.getComments().get(position).getComment_id()));
    }

    @Override
    public void onEditButtonClickListener(int position,String comment) {
        editingIndex=position;
        EditCommentDialog dialog = new EditCommentDialog(comment);
        dialog.show(mActivity.getSupportFragmentManager(),"");
    }

    @Override
    public void onDeleteButtonClickListener(int position) {
        viewModel.deleteCommentCommand( new LikeCommentData(user.getEmail(),user.getTokenID(),eventID,comments.getComments().get(position).getComment_id()));
        deleteIndex=position;
    }

    @Override
    public void onDialogPositiveClick(String comment) {
        commentToAdd=comment;
        viewModel.addCommentCommand(new CommentData(user.getEmail(),user.getTokenID(),eventID,comment));
    }

    @Override
    public void onEditDialogPositiveClick(String comment) {
        editedComment=comment;
        viewModel.editCommentCommand(new EditCommentData(user.getEmail(),user.getTokenID(),eventID,comments.getComments().get(editingIndex).getComment_id(),comment));
    }
}