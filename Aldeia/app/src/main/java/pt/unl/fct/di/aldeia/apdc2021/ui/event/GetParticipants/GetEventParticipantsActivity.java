package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetParticipants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetChatCommentUnit;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventChatReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.ParticipantInfoUnit;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat.EventChatAdapter;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat.EventChatViewModelFactory;

public class GetEventParticipantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GetEventParticipantsViewModel viewModel;
    private GetEventParticipantsActivity mActivity;
    private UserLocalStore storage;
    private UserAuthenticated user;
    private String eventID;
    private GetParticipantsReply participants;
    private ParticipantsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_participants);
        Context context=this;
        recyclerView=findViewById(R.id.eventParticipantsRecyclerView);
        TextView noParticipants=findViewById(R.id.getParticipantsNoParticipants);
        Button loadMoreParticipants=findViewById(R.id.loadMoreParticipantsButton);
        viewModel=new ViewModelProvider(this, new GetEventParticipantsViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(GetEventParticipantsViewModel.class);
        mActivity=this;
        adapter=null;
        storage= new UserLocalStore(this);
        user=storage.getLoggedInUser();
        eventID=getIntent().getStringExtra("eventID");
        viewModel.getParticipants(new GetEventParticipantsData(user.getEmail(), user.getTokenID(), eventID,0));
        noParticipants.setVisibility(View.GONE);

        viewModel.getEventParticipants().observe(this, new Observer<GetEventParticipantsResult>() {
            @Override
            public void onChanged(GetEventParticipantsResult getEventParticipantsResult) {
                if(getEventParticipantsResult==null){
                    return;
                }
                if(getEventParticipantsResult.getError()!=null){
                    Toast.makeText(mActivity, getEventParticipantsResult.getError() ,Toast.LENGTH_SHORT).show();
                }
                if(getEventParticipantsResult.getSuccess()!=null){
                    if(participants==null){
                        participants =getEventParticipantsResult.getSuccess();
                        if(participants.getParticipants().size()==0){
                            noParticipants.setVisibility(View.VISIBLE);
                        }
                    }else{
                        List<ParticipantInfoUnit> original=participants.getParticipants();
                        GetParticipantsReply loadedReply=getEventParticipantsResult.getSuccess();
                        List<ParticipantInfoUnit> loadedParticipants=loadedReply.getParticipants();
                        for(int i =0;i<loadedParticipants.size();i++){
                            original.add(loadedParticipants.get(i));
                        }
                        participants=new GetParticipantsReply(original,loadedReply.getResults(),loadedReply.getCursor());
                    }
                    adapter = new ParticipantsAdapter(context,participants.getParticipants());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                }
            }
        });
        loadMoreParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(participants.getResults().equals("MORE_RESULTS_AFTER_LIMIT")){
                    viewModel.getParticipants(new GetEventParticipantsData(user.getEmail(),user.getTokenID(),eventID,participants.getCursor())); //TODO
                }else{
                    Toast.makeText(mActivity, R.string.event_no_more_users,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}