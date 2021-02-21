package app.spitech.ui.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.models.DataBin;
import app.spitech.ui.home.Home;
import app.spitech.ui.settings.ProfileEdit;
import app.spitech.appSDK.SpiTech;

/**
 * Created by Hp on 7/17/2016.
 */

public class TodayJoinedAdapter extends RecyclerView.Adapter<TodayJoinedAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    Home parent;

    // RecyclerView recyclerView;
    public TodayJoinedAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        this.parent=(Home)context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recently_joined_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(SpiTech.getInstance().getTrimedString(data.getName(),13));
        SpiTech.getInstance().loadRoundedImage(context,data.getImage(), holder.photo,R.drawable.ic_user);
        if(data.getRowId().equalsIgnoreCase(parent.session.getUserId())){
            holder.name.setTextColor(context.getResources().getColor(R.color.green));
        }
        holder.photo.setOnClickListener(view -> {
            if(data.getRowId().equalsIgnoreCase(parent.session.getUserId())){
                Intent intent=new Intent(context, ProfileEdit.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView photo;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            photo =itemView.findViewById(R.id.photo);
        }
    }

}