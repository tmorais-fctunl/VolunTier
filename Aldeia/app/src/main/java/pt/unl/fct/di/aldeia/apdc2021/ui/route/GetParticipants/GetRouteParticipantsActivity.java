package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetParticipants;

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
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetEventParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetParticipantsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteParticipantsData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.ParticipantInfoUnit;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;

public class GetRouteParticipantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GetRouteParticipantsViewModel viewModel;
    private GetRouteParticipantsActivity mActivity;
    private UserLocalStore storage;
    private UserAuthenticated user;
    private String routeID;
    private GetParticipantsReply participants;
    private RouteParticipantsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_route_participants);
        Context context=this;
        recyclerView=findViewById(R.id.routeParticipantsRecyclerView);
        TextView noParticipants=findViewById(R.id.routeGetParticipantsNoParticipants);
        Button loadMoreParticipants=findViewById(R.id.routeLoadMoreParticipantsButton);
        viewModel=new ViewModelProvider(this, new GetRouteParticipantsViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(GetRouteParticipantsViewModel.class);
        mActivity=this;
        adapter=null;
        storage= new UserLocalStore(this);
        user=storage.getLoggedInUser();
        routeID=getIntent().getStringExtra("routeID");
        viewModel.getParticipants(new GetRouteParticipantsData(user.getEmail(), user.getTokenID(), routeID,0));
        noParticipants.setVisibility(View.GONE);

        viewModel.getRouteParticipants().observe(this, new Observer<GetRouteParticipantsResult>() {
            @Override
            public void onChanged(GetRouteParticipantsResult getRouteParticipantsResult) {
                if(getRouteParticipantsResult==null){
                    return;
                }
                if(getRouteParticipantsResult.getError()!=null){
                    Toast.makeText(mActivity, getRouteParticipantsResult.getError() ,Toast.LENGTH_SHORT).show();
                }
                if(getRouteParticipantsResult.getSuccess()!=null){
                    if(participants==null){
                        participants =getRouteParticipantsResult.getSuccess();
                        if(participants.getParticipants().size()==0){
                            noParticipants.setVisibility(View.VISIBLE);
                        }
                    }else{
                        List<ParticipantInfoUnit> original=participants.getParticipants();
                        GetParticipantsReply loadedReply=getRouteParticipantsResult.getSuccess();
                        List<ParticipantInfoUnit> loadedParticipants=loadedReply.getParticipants();
                        for(int i =0;i<loadedParticipants.size();i++){
                            original.add(loadedParticipants.get(i));
                        }
                        participants=new GetParticipantsReply(original,loadedReply.getResults(),loadedReply.getCursor());
                    }
                    adapter = new RouteParticipantsAdapter(context,participants.getParticipants());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                }
            }
        });
        loadMoreParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(participants.getResults().equals("MORE_RESULTS_AFTER_LIMIT")){
                    viewModel.getParticipants(new GetRouteParticipantsData(user.getEmail(),user.getTokenID(),routeID,participants.getCursor())); //TODO
                }else{
                    Toast.makeText(mActivity, R.string.event_no_more_users,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}