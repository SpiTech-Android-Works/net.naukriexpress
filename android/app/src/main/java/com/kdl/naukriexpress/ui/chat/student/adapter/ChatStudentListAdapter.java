package com.kdl.naukriexpress.ui.chat.student.adapter;

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
import com.kdl.naukriexpress.ui.chat.student.Chat;


public class ChatStudentListAdapter extends RecyclerView.Adapter<ChatStudentListAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    ArrayList<DataBin> originalList;
    Context context;
    AppSession session;

    public ChatStudentListAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        this.originalList=list1;
        this.session=new AppSession(context1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_row, parent, false);
        return new ChatStudentListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        holder.name.setText(ConvertTo.toTitleCase(data.getName()));
        holder.message.setText(data.getMessage());
        holder.datetime.setText(data.getDate());
        if(Validation.isNotEmpty(data.getImage())){
            String url = AppConfig.mediaCustomer+ data.getImage();
            SpiTech.getInstance().loadImage(context,url, holder.image);
        }else{
            holder.photo_name.setText(SpiTech.getInstance().getNamedPhoto(data.getName()));
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent=new Intent(context, Chat.class);
            intent.putExtra("name",data.getName());
            intent.putExtra("to_user_id",data.getRowId());
            intent.putExtra("to_user_image", data.getImage());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,message,datetime,photo_name;
        public CircleImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            photo_name = itemView.findViewById(R.id.photo_name);
            message = itemView.findViewById(R.id.message);
            datetime = itemView.findViewById(R.id.datetime);
            image = itemView.findViewById(R.id.image);
        }
    }

    //-------------search filter------------
    public void filter(String text){
        ArrayList<DataBin> temp = new ArrayList();
        if(Validation.isNotEmpty(text)){
            Log.e("searching",text);
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