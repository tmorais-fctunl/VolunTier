package pt.unl.fct.di.aldeia.apdc2021.ui.leaderboard;

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
import pt.unl.fct.di.aldeia.apdc2021.data.model.RankingData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.YourData;
import pt.unl.fct.di.aldeia.apdc2021.ui.community.OnLookUpItemClickListener;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.MyViewHolder> {

    List<RankingData> rankingData;
    Context context;
    OnLookUpItemClickListener onLookUpItemClickListener = null;

    public LeaderboardAdapter(OnLookUpItemClickListener listener, Context context, List<RankingData> ranking) {
        this.context=context;
        rankingData=ranking;
        this.onLookUpItemClickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leaderboard_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        RankingData cur = rankingData.get(position);
        holder.rank.setText(String.valueOf(position+1));
        holder.points.setText(String.valueOf(cur.getScore()));
        holder.username.setText(cur.getUsername());
        holder.fullName.setText(cur.getFull_name());
        holder.email.setText(cur.getEmail());
        String encodedImage = cur.getPic_64();
        if(encodedImage != null) {
            byte[] decodedString = Base64.decode(encodedImage.substring(21), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(decodedByte);
        }
        else {
            holder.image.setImageResource(R.drawable.ic_profile);
        }
        holder.lookUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLookUpItemClickListener.onLookUpItemClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rankingData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView username, fullName, email, rank, points;
        ImageView image;
        ImageButton lookUp;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.leaderboardRank);
            points = itemView.findViewById(R.id.leaderboardPoints);
            username = itemView.findViewById(R.id.participantsUsername);
            fullName = itemView.findViewById(R.id.leaderboardFullName);
            email = itemView.findViewById(R.id.participantsEmail);
            image = itemView.findViewById(R.id.participantsRowImage);
            lookUp = itemView.findViewById(R.id.leaderboard_lookUp_button);
        }
    }
}