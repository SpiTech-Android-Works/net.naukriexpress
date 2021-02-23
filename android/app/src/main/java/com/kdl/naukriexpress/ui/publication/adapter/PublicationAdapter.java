package com.kdl.naukriexpress.ui.publication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.appSDK.StaticMethods;
import com.kdl.naukriexpress.models.BookModel;
import com.kdl.naukriexpress.ui.publication.Publication;


public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<BookModel> list;
    Context context;

    // RecyclerView recyclerView;
    public PublicationAdapter(Context context1, ArrayList<BookModel> list1) {
        this.context = context1;
        this.list = list1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new PublicationAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BookModel data = list.get(position);
        holder.name.setText(StaticMethods.getSubString(data.getName(),20));
        holder.rate.setText("Rs." + data.getRate());
        String url = AppConfig.mediaProduct + data.getImage();
        SpiTech.getInstance().loadImage(context,url,holder.image);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Publication.class);
            intent.putExtra("type","59");
            intent.putExtra("package_id", data.getRowId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.btnBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(context, Publication.class);
            intent.putExtra("type","59");
            intent.putExtra("package_id", data.getRowId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name, rate,btnBuyNow;
        public RelativeLayout linearLayoutView;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            rate = itemView.findViewById(R.id.rate);
            btnBuyNow = itemView.findViewById(R.id.btnBuyNow);
        }
    }

}