package pt.unl.fct.di.aldeia.apdc2021.ui.route.GetParticipants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pt.unl.fct.di.aldeia.apdc2021.R;
import pt.unl.fct.di.aldeia.apdc2021.data.model.ParticipantInfoUnit;

public class RouteParticipantsAdapter extends RecyclerView.Adapter<RouteParticipantsAdapter.MyViewHolder> {

    Context ct;
    List<ParticipantInfoUnit> participants;

    public RouteParticipantsAdapter(Context ct, List<ParticipantInfoUnit> participants){
        this.ct=ct;
        this.participants=participants;
    }




    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(ct);
        View view=inflater.inflate(R.layout.route_participants_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        ParticipantInfoUnit unit=participants.get(position);
        if(!unit.getPic().equals("")) {
            byte[] decodedString = Base64.decode(unit.getPic().substring(21), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(decodedByte);
        }
        else {
            holder.image.setImageResource(R.drawable.ic_profile);
        }
        holder.email.setText(unit.getEmail());
        holder.role.setVisibility(View.GONE);
        if(unit.getRole().equals("OWNER")){
            holder.role.setVisibility(View.VISIBLE);
        }
        else if(unit.getRole().equals("MOD")){
            holder.role.setImageResource(R.drawable.ic_baseline_construction_24);
            holder.role.setVisibility(View.VISIBLE);

        }
        holder.username.setText(unit.getUsername());

    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView username,email;
        public ImageView image,role;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.routeparticipantsUsername);
            email=itemView.findViewById(R.id.routeparticipantsEmail);
            role=itemView.findViewById(R.id.routeparticipantsRoleImage);
            image=itemView.findViewById(R.id.routeparticipantsRowImage);
        }
    }

}