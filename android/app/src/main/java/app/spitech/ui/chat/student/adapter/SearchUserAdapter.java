package app.spitech.ui.chat.student.adapter;

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
import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.ConvertTo;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;
import app.spitech.models.DataBin;
import app.spitech.ui.chat.student.Chat;


public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    ArrayList<DataBin> originalList;
    Context context;
    AppSession session;

    public SearchUserAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        this.originalList= list1;
        this.session=new AppSession(context1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new SearchUserAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        holder.name.setText(ConvertTo.toTitleCase(data.getName()));
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
            intent.putExtra("to_user_image",data.getImage());
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
        public TextView name,message,photo_name;
        public CircleImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            photo_name = itemView.findViewById(R.id.photo_name);
            image = itemView.findViewById(R.id.image);
        }
    }

    //-------------search filter------------
    public void filter(String text){
        ArrayList<DataBin> temp = new ArrayList();
        Log.e("searching1",text);
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