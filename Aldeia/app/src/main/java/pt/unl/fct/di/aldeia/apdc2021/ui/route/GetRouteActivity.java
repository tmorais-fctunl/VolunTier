package pt.unl.fct.di.aldeia.apdc2021.ui.route;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pt.unl.fct.di.aldeia.apdc2021.App;
import pt.unl.fct.di.aldeia.apdc2021.DefaultResult;
import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetRouteReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.LeaveRouteData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteRatingData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserAuthenticated;
import pt.unl.fct.di.aldeia.apdc2021.data.model.UserLocalStore;
import pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn.RoomViewModel;
import pt.unl.fct.di.aldeia.apdc2021.ui.route.GetChat.RouteChatActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.route.GetParticipants.GetRouteParticipantsActivity;
import pt.unl.fct.di.aldeia.apdc2021.ui.route.GetParticipants.RateRouteViewModelFactory;

public class GetRouteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GetRouteViewModel viewModel;
    private RateRouteViewModel rateRouteViewModel;
    private UserLocalStore storage;
    private GetRouteReply reply;
    private GetRouteActivity mActivity;
    private RouteEventsAdapter adapter;
    private RoomViewModel roomViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String route_id = null;
        if (extras != null) {
            route_id = extras.getString("route_id");
        }
        setContentView(R.layout.activity_get_route);
        storage= new UserLocalStore(this);
        UserAuthenticated user=storage.getLoggedInUser();
        viewModel = new ViewModelProvider(this, new GetRouteViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(GetRouteViewModel.class);
        rateRouteViewModel = new ViewModelProvider(this, new RateRouteViewModelFactory(((App) getApplication()).getExecutorService()))
                .get(RateRouteViewModel.class);
        mActivity=this;
        roomViewModel= new RoomViewModel(mActivity.getApplication());
        Context context=this;
        final EditText routeNameEditText = findViewById(R.id.route_page_name);
        final EditText routeCreatorEditText = findViewById(R.id.route_page_creator);
        final EditText routeDescriptionEditText = findViewById(R.id.route_page_description);
        final EditText routeCreationDateEditText = findViewById(R.id.route_page_creation_date);
        final EditText routeParticipantsEditText = findViewById(R.id.route_page_participants);
        recyclerView= findViewById(R.id.route_page_recycler);
        final TextView routeAvgRatingTextView=findViewById(R.id.route_average_rating);
        final ProgressBar routeProgressBar=findViewById(R.id.routeProgressBar);
        final Spinner routeRatingSpinner = findViewById(R.id.route_rating_spinner);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.difficulties));
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeRatingSpinner.setAdapter(adapterSpinner);
        final Button routeDeleteButton=findViewById(R.id.route_delete_button);
        final Button routeSaveButton=findViewById(R.id.route_page_save);
        final Button routeCancelButton=findViewById(R.id.route_page_cancel);
        final Button routeChatButton=findViewById(R.id.route_page_comments);
        final ImageView routeParticipants = findViewById(R.id.event_participants_button);
        adapter=null;
        viewModel.getRoute(new GetRouteData(user.getEmail(),user.getTokenID(),route_id));

        viewModel.getGetRouteResult().observe(this, new Observer<GetRouteResult>() {
            @Override
            public void onChanged(GetRouteResult getRouteResult) {
                if(getRouteResult==null){
                    return;
                }
                if(getRouteResult.getError()!=null){
                    Toast.makeText(mActivity, getRouteResult.getError(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                if(getRouteResult.getSuccess()!=null){
                    routeProgressBar.setVisibility(View.GONE);
                    reply=getRouteResult.getSuccess();
                    Double avgRatingNumber=reply.getAvg_rating();
                    BigDecimal bigDecimal = new BigDecimal(Double.toString(avgRatingNumber));
                    bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
                    String avgRating=routeAvgRatingTextView.getText().toString().concat(" ".concat(bigDecimal.toString()));
                    routeAvgRatingTextView.setText(avgRating);
                    routeCreatorEditText.setText(reply.getCreator());
                    routeDescriptionEditText.setText(reply.getDescription());
                    routeParticipantsEditText.setText(String.valueOf(reply.getNum_participants()));
                    String[] date= reply.getCreation_date().split("T");
                    String time=date[1].substring(0,5);
                    routeCreationDateEditText.setText(date[0].concat(" ".concat(time)));
                    routeNameEditText.setText(reply.getRoute_name());
                    String value = String.valueOf(reply.getMy_rating()).substring(0,1);
                    routeRatingSpinner.setSelection(adapterSpinner.getPosition(value));
                    routeCreationDateEditText.setFocusable(false);
                    routeCreatorEditText.setFocusable(false);
                    routeDescriptionEditText.setFocusable(false);
                    routeNameEditText.setFocusable(false);
                    routeCreationDateEditText.setClickable(false);
                    routeCreatorEditText.setClickable(false);
                    routeDescriptionEditText.setClickable(false);
                    routeNameEditText.setClickable(false);
                    adapter = new RouteEventsAdapter(context,reply.getEvents());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                    if(reply.getStatus().equals("PARTICIPANT")){
                        routeDeleteButton.setText("Leave");
                    }else if(reply.getStatus().equals("NON_PARTICIPANT")){
                        routeDeleteButton.setText("Join");
                    }
                }
            }
        });

        rateRouteViewModel.getRateRouteResult().observe(this, new Observer<RateRoutResult>() {
            @Override
            public void onChanged(RateRoutResult rateRoutResult) {
                if(rateRoutResult==null){
                    return;
                }
                if(rateRoutResult.getError()!=null){
                    Toast.makeText(mActivity, rateRoutResult.getError(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                if(rateRoutResult.getSuccess()!=null){
                    Toast.makeText(mActivity, rateRoutResult.getSuccess(), Toast.LENGTH_SHORT).show();
                    routeRatingSpinner.setSelection(adapterSpinner.getPosition(String.valueOf(reply.getMy_rating())));
                }
            }
        });

        routeCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        routeParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, GetRouteParticipantsActivity.class);
                intent.putExtra("routeID",reply.getRoute_id());
                startActivity(intent);
            }
        });

        routeChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, RouteChatActivity.class);
                intent.putExtra("routeID",reply.getRoute_id());
                intent.putExtra("status",reply.getStatus());
                startActivity(intent);
            }
        });

        routeDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reply.getStatus().equals("CREATOR")){
                    viewModel.deleteRoute(new GetRouteData(user.getEmail(),user.getTokenID(),reply.getRoute_id()));
                }else if(reply.getStatus().equals("PARTICIPANT")){
                    viewModel.leaveRoute(new LeaveRouteData(user.getEmail(),user.getTokenID(),reply.getRoute_id(),user.getEmail()));
                }else if(reply.getStatus().equals("NON_PARTICIPANT")){
                    viewModel.participateRoute(new GetRouteData(user.getEmail(),user.getTokenID(),reply.getRoute_id()));
                }
            }
        });

        viewModel.getDeleteRouteResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult defaultResult) {
                if(defaultResult==null){
                    return;
                }
                if(defaultResult.getError()!=null){
                    Toast.makeText(mActivity, defaultResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if(defaultResult.getSuccess()!=null){
                    Toast.makeText(mActivity, defaultResult.getSuccess(), Toast.LENGTH_SHORT).show();
                    roomViewModel.deleteCrossOverFromRouteID(reply.getRoute_id());
                    roomViewModel.deleteRoute(reply.getRoute_id());
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("operation","Delete");
                    returnIntent.putExtra("id",reply.getRoute_id());
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });
        viewModel.getParticipateRouteResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult defaultResult) {
                if(defaultResult==null){
                    return;
                }
                if(defaultResult.getError()!=null){
                    Toast.makeText(mActivity, defaultResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if(defaultResult.getSuccess()!=null){
                    Toast.makeText(mActivity, defaultResult.getSuccess(), Toast.LENGTH_SHORT).show();
                    routeDeleteButton.setText("Leave");
                    reply.setStatus("PARTICIPANT");
                    String text = routeParticipantsEditText.getText().toString();
                    int conversion=Integer.parseInt(text);
                    conversion++;
                    routeParticipantsEditText.setText(String.valueOf(conversion));
                }
            }
        });

        viewModel.getLeaveRouteResult().observe(this, new Observer<DefaultResult>() {
            @Override
            public void onChanged(DefaultResult defaultResult) {
                if(defaultResult==null){
                    return;
                }
                if(defaultResult.getError()!=null){
                    Toast.makeText(mActivity, defaultResult.getError(), Toast.LENGTH_SHORT).show();
                }
                if(defaultResult.getSuccess()!=null){
                    Toast.makeText(mActivity, defaultResult.getSuccess(), Toast.LENGTH_SHORT).show();
                    routeDeleteButton.setText("Join");
                    reply.setStatus("NON_PARTICIPANT");
                    String text = routeParticipantsEditText.getText().toString();
                    int conversion=Integer.parseInt(text);
                    conversion--;
                    routeParticipantsEditText.setText(String.valueOf(conversion));
                }
            }
        });

        rateRouteViewModel.getRateRouteResult().observe(this, new Observer<RateRoutResult>() {
            @Override
            public void onChanged(RateRoutResult rateRoutResult) {
                if (rateRoutResult == null) {
                    return;
                }
                if (rateRoutResult.getError() != null) {
                    Toast.makeText(mActivity, "Rating Update Unsuccessful", Toast.LENGTH_SHORT).show();
                }
                if (rateRoutResult.getSuccess() != null) {
                    Toast.makeText(mActivity, "Rating Update Successful", Toast.LENGTH_SHORT).show();
                }
            }
        });

        routeSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = Float.parseFloat(routeRatingSpinner.getSelectedItem().toString());
                rateRouteViewModel.rateRoute(new RouteRatingData(user.getEmail(), user.getTokenID(), reply.getRoute_id(), rating));
            }
        });

    }
}