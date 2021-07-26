package pt.unl.fct.di.aldeia.apdc2021.ui.event.GetChat;

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
import pt.unl.fct.di.aldeia.apdc2021.data.model.GetChatCommentUnit;

public class EventChatAdapter extends RecyclerView.Adapter<EventChatAdapter.MyViewHolder> {

    private Context ct;
    private List<GetChatCommentUnit> comments;
    private LikeClickEventInterface adapterItemClickListener = null;
    private String userStatus;
    private String userUsername;

    public EventChatAdapter(LikeClickEventInterface listener ,Context ct, List<GetChatCommentUnit> comments,String userStatus,String userUsername){
        this.comments=comments;
        this.ct=ct;
        this.adapterItemClickListener=listener;
        this.userStatus=userStatus;
        this.userUsername=userUsername;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(ct);
        View view=inflater.inflate(R.layout.event_chat_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        GetChatCommentUnit unit=comments.get(position);
        if(unit.getTimestamp().equals("Just Now")){
            holder.timestamp.setText(unit.getTimestamp());
        }else{
            String[] date= unit.getTimestamp().split("T");
            String time=date[1].substring(0,5);
            holder.timestamp.setText(date[0].concat(" ".concat(time)));
        }
        holder.username.setText(unit.getUsername());
        holder.comment.setText(unit.getComment());
        holder.numLikes.setText(String.valueOf(unit.getLikes()));
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterItemClickListener.onLikeButtonClickListener(position);
                int likes=Integer.parseInt(holder.numLikes.getText().toString());
                if(unit.isLike_status()){
                    likes--;
                    unit.setLike_status(false);
                }else{
                    likes++;
                    unit.setLike_status(true);
                }
                holder.numLikes.setText(String.valueOf(likes));
            }
        });


        if(userStatus.equals("OWNER")||userStatus.equals("MOD")||userUsername.equals(unit.getUsername())){
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterItemClickListener.onDeleteButtonClickListener(position);
                }
            });
        }else{
            holder.deleteButton.setClickable(false);
            holder.deleteButton.setVisibility(View.GONE);
        }

        if(userUsername.equals(unit.getUsername())){
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterItemClickListener.onEditButtonClickListener(position,unit.getComment());
                }
            });
        }else{
            holder.editButton.setClickable(false);
            holder.editButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView username,timestamp,comment,numLikes;
        public ImageButton likeButton,deleteButton,editButton;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.row_route_event_name);
            timestamp=itemView.findViewById(R.id.row_route_event_participants);
            comment=itemView.findViewById(R.id.row_route_event_start);
            numLikes=itemView.findViewById(R.id.commentNumLikes);
            likeButton=itemView.findViewById(R.id.getChatLikeButton);
            deleteButton=itemView.findViewById(R.id.getChatDeleteButton);
            editButton=itemView.findViewById(R.id.getChatEditButton);
        }
    }
}
