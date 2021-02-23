package com.kdl.naukriexpress.ui.chat.group.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.ConvertTo;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.appSDK.Validation;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.chat.group.GroupChat;


public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    ArrayList<DataBin> originalList;
    Context context;
    AppSession session;

    public GroupListAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        this.originalList=list1;
        this.session=new AppSession(context1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_row, parent, false);
        return new GroupListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(ConvertTo.toTitleCase(data.getName()));
        holder.member_count.setText(ConvertTo.toTitleCase(data.getCount())+" members");
        if(Validation.isNotEmpty(data.getImage())){
            String url = AppConfig.mediaGroup+ data.getImage();
            SpiTech.getInstance().loadImage(context,url, holder.image);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent=new Intent(context, GroupChat.class);
            intent.putExtra("group_name",data.getName());
            intent.putExtra("group_id",data.getRowId());
            intent.putExtra("group_image", data.getImage());
            intent.putExtra("member_count", data.getCount());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,member_count;
        public CircleImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            member_count= itemView.findViewById(R.id.member_count);
            image = itemView.findViewById(R.id.image);
        }
    }

    //-------------search filter------------
    public void filter(String text){
        ArrayList<DataBin> temp = new ArrayList();
        Log.e("searching",text);
        if(Validation.isNotEmpty(text)){
            for(DataBin d: originalList){
                if(d.getName().toLowerCase().contains(text.toLowerCase())){
                    temp.add(d);
                }
            }
            list=temp;
        }else{
            list=originalList;
        }
        notifyDataSetChanged();
    }

}