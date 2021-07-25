package pt.unl.fct.di.aldeia.apdc2021.ui.event;

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
import java.util.Set;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.CausesData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingData;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.OnLookUpItemClickListener;

public class EventProfileAdapter extends RecyclerView.Adapter<EventProfileAdapter.MyViewHolder> {

    Set<String> events;
    Context context;
    SpecialOnLookUpItemClickListener onLookUpItemClickListener = null;

    public EventProfileAdapter(SpecialOnLookUpItemClickListener listener, Context context, Set<String> events) {
        this.context=context;
        this.events=events;
        this.onLookUpItemClickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.profile_event_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Object[] eventArray = events.toArray();
        String event_id = String.valueOf(eventArray[position]);
        holder.eventName.setText(event_id);
        holder.lookUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLookUpItemClickListener.specialOnLookUpItemClickListener(event_id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView eventName;
        ImageButton lookUp;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.profile_event_row_id);
            lookUp = itemView.findViewById(R.id.profile_row_event_look_more);
        }
    }
}