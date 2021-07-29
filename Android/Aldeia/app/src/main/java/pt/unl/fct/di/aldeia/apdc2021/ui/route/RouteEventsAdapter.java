package pt.unl.fct.di.aldeia.apdc2021.ui.route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RouteEventInfo;

public class RouteEventsAdapter extends RecyclerView.Adapter<RouteEventsAdapter.MyViewHolder> {

    private Context ct;
    private List<RouteEventInfo> events;

    public RouteEventsAdapter(Context ct, List<RouteEventInfo> events){
        this.events=events;
        this.ct=ct;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(ct);
        View view=inflater.inflate(R.layout.route_event_row,parent,false);
        return new RouteEventsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RouteEventsAdapter.MyViewHolder holder, int position) {
        RouteEventInfo unit=events.get(position);
        holder.name.setText(unit.getName());
        holder.numParticipants.setText("Participants: ".concat(String.valueOf(unit.getNum_participants())));
        String[] vec1= unit.getStart_date().split("T");
        String time1=vec1[1].substring(0,5);
        holder.startDate.setText("From: ".concat(vec1[0].concat(" ".concat(time1))));
        String[] vec2= unit.getEnd_date().split("T");
        String time2=vec1[1].substring(0,5);
        holder.endDate.setText("To: ".concat(vec2[0].concat(" ".concat(time2))));

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name,numParticipants,startDate,endDate;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.row_route_event_name);
            numParticipants=itemView.findViewById(R.id.row_route_event_participants);
            startDate=itemView.findViewById(R.id.row_route_event_start);
            endDate=itemView.findViewById(R.id.row_route_event_end);
        }
    }
}

