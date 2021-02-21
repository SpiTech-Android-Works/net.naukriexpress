package app.spitech.ui.chat.group.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    ArrayList<DataBin> originalList;
    Context context;
    AppSession session;
    private ArrayList<String> aSelectedUser;

    public AddMemberAdapter(Context context1, ArrayList<DataBin> list1) {
        aSelectedUser=new ArrayList<String>();
        this.context = context1;
        this.list = list1;
        this.originalList = list1;
        this.session=new AppSession(context1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<String> getSelectedItem() {
        return aSelectedUser;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_group_member_row, parent, false);
        return new AddMemberAdapter.ViewHolder(itemView);
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

        holder.itemView.setOnClickListener(view -> {
            if(aSelectedUser.contains(data.getRowId())){
                holder.imgCheck.setVisibility(View.GONE);
                holder.itemView.setAlpha(1F);
                aSelectedUser.remove(data.getRowId());
            }else{
                holder.imgCheck.setVisibility(View.VISIBLE);
                holder.itemView.setAlpha(0.6F);
                aSelectedUser.add(data.getRowId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,message,photo_name;
        public CircleImageView image;
        public ImageView imgCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            photo_name = itemView.findViewById(R.id.photo_name);
            image = itemView.findViewById(R.id.image);
            imgCheck = itemView.findViewById(R.id.imgCheck);
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