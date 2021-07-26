package pt.unl.fct.di.aldeia.apdc2021.ui.shop;

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
import pt.unl.fct.di.aldeia.apdc2021.ui.community.OnLookUpItemClickListener;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MyViewHolder> {

    List<CausesData> causesDataList;
    Context context;
    OnLookUpItemClickListener onLookUpItemClickListener = null;

    public ShopAdapter(OnLookUpItemClickListener listener, Context context, List<CausesData> causes) {
        this.context=context;
        causesDataList=causes;
        this.onLookUpItemClickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shop_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        CausesData cur = causesDataList.get(position);
        holder.causeName.setText(cur.getName());
        holder.company.setText(cur.getCompany_name());
        holder.website.setText(cur.getWebsite());
        /*String encodedImage = cur.get();
        if(encodedImage != null) {
            byte[] decodedString = Base64.decode(encodedImage.substring(21), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(decodedByte);
        }
        else {*/
        holder.image.setImageResource(R.drawable.ic_baseline_local_florist_24);
        //}
        holder.lookUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLookUpItemClickListener.onLookUpItemClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return causesDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView causeName, company, website;
        ImageView image;
        ImageButton lookUp;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            causeName = itemView.findViewById(R.id.shopRowCauseName);
            website = itemView.findViewById(R.id.shopRowWebsite);
            company = itemView.findViewById(R.id.shopRowCompany);
            image = itemView.findViewById(R.id.shopRowImage);
            lookUp = itemView.findViewById(R.id.shopRowOpenCause);
        }
    }
}