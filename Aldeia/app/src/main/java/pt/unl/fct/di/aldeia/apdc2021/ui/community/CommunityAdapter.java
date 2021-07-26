package pt.unl.fct.di.aldeia.apdc2021.ui.community;

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
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.SearchUserData;
import pt.unl.fct.di.aldeia.apdc2021.data.model.YourData;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> {

    List<SearchData> rankingData;
    Context context;
    OnLookUpItemClickListener onLookUpItemClickListener = null;

    public CommunityAdapter(OnLookUpItemClickListener listener, Context context, List<SearchData> ranking) {
        this.context=context;
        rankingData=ranking;
        this.onLookUpItemClickListener=listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.community_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        SearchData cur = rankingData.get(position);
        holder.profile.setText(cur.getProfile());
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
        TextView username, fullName, email, profile;
        ImageView image;
        ImageButton lookUp;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.communityProfile);
            username = itemView.findViewById(R.id.communityUsername);
            fullName = itemView.findViewById(R.id.communityFullName);
            email = itemView.findViewById(R.id.communityEmail);
            image = itemView.findViewById(R.id.communityRowImage);
            lookUp = itemView.findViewById(R.id.community_lookUp_button);
        }
    }
}