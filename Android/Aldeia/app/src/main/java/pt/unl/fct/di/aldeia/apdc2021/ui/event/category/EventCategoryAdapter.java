package pt.unl.fct.di.aldeia.apdc2021.ui.event.category;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReply;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchEventsReplyUnit;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.OnLookUpItemClickListener;
import pt.unl.fct.di.aldeia.apdc2021.ui.event.SpecialOnLookUpItemClickListener;

public class EventCategoryAdapter extends RecyclerView.Adapter<EventCategoryAdapter.MyViewHolder> {

    List<SearchEventsReplyUnit> events;
    Context context;
    SpecialOnLookUpItemClickListener onLookUpItemClickListener = null;

    public EventCategoryAdapter(SpecialOnLookUpItemClickListener listener, Context context, List<SearchEventsReplyUnit> events) {
        this.context=context;
        this.events=events;
        this.onLookUpItemClickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_event_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        SearchEventsReplyUnit cur = events.get(position);
        holder.eventName.setText(cur.getName());
        holder.number.setText(String.valueOf(cur.getNum_participants()));
        String date = cur.getStart_date().substring(0,10);
        holder.start.setText(date);
        date = cur.getEnd_date().substring(0,10);
        holder.end.setText(date);
        holder.lookUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLookUpItemClickListener.specialOnLookUpItemClickListener(cur.getEvent_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView eventName, number, start, end;
        ImageButton lookUp;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.activity_event_row_name);
            number = itemView.findViewById(R.id.activity_event_row_num);
            start = itemView.findViewById(R.id.activity_event_row_start);
            end = itemView.findViewById(R.id.activity_event_row_end);
            lookUp = itemView.findViewById(R.id.activity_row_event_look_more);
        }
    }
}