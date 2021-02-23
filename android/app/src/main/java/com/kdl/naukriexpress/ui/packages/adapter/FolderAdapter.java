package com.kdl.naukriexpress.ui.packages.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.Interfaces.CommonInterface;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.packages.ContentPDF;
import com.kdl.naukriexpress.ui.packages.ContentTest;
import com.kdl.naukriexpress.ui.packages.ProductDetails;
import com.kdl.naukriexpress.ui.packages.StoreBottomSheet;
import com.kdl.naukriexpress.ui.packages.ContentVideo;


public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    AppSession session;
    CommonInterface commonInterface;
    String package_name="";

    public FolderAdapter(Context context1, ArrayList<DataBin> list1, CommonInterface commonInterface,String package_name) {
        this.context = context1;
        this.list = list1;
        this.session = new AppSession(context1);
        this.commonInterface=commonInterface;
        this.package_name=package_name;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_row, parent, false);
        return new FolderAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(data.getName());

        if (data.getIsPurchased().equalsIgnoreCase("1")) {
            holder.btnLock.setVisibility(View.GONE);
            holder.btnNext.setVisibility(View.VISIBLE);
        }else{
            holder.btnLock.setVisibility(View.VISIBLE);
            holder.btnNext.setVisibility(View.GONE);
        }

        String item_count = "";
        if (data.getType().equalsIgnoreCase("pdf")) {
            holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_pdf));
            item_count = "PDF File : ";
        } else if (data.getType().equalsIgnoreCase("test")) {
            holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_test));
            item_count = "Test Series : ";
        } else if (data.getType().equalsIgnoreCase("audio")) {
            holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_audio));
            item_count = "Audio Files : ";
        } else if (data.getType().equalsIgnoreCase("video")) {
            holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_video));
            item_count = "Videos : ";
        }
        holder.item_count.setText(item_count + data.getCount());

        holder.itemView.setOnClickListener(v -> {
            showContent(data);
        });
        holder.btnNext.setOnClickListener(v -> {
            showContent(data);
        });
        holder.btnLock.setOnClickListener(v -> {
            showContent(data);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void showContent(DataBin data){
        if (data.getIsPurchased().equalsIgnoreCase("1")) {
            if (data.getType().equalsIgnoreCase("video")) {
                Intent intent = new Intent(context, ContentVideo.class);
                intent.putExtra("folder_id", data.getRowId());
                intent.putExtra("package_name", package_name);
                intent.putExtra("package_id", data.getPackageId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (data.getType().equalsIgnoreCase("pdf")) {
                Intent intent = new Intent(context, ContentPDF.class);
                intent.putExtra("folder_id", data.getRowId());
                intent.putExtra("package_name", package_name);
                intent.putExtra("package_id", data.getPackageId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (data.getType().equalsIgnoreCase("test")) {
                Intent intent = new Intent(context, ContentTest.class);
                intent.putExtra("folder_id", data.getRowId());
                intent.putExtra("package_name", package_name);
                intent.putExtra("package_id", data.getPackageId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }else{
            StoreBottomSheet bottomSheet=new StoreBottomSheet(commonInterface,data.getRate());
            bottomSheet.show(((ProductDetails)context).getSupportFragmentManager(),bottomSheet.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, item_count;
        public ImageView image, btnNext, btnLock;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            item_count = itemView.findViewById(R.id.item_count);
            image = itemView.findViewById(R.id.image);
            btnNext = itemView.findViewById(R.id.btnNext);
            btnLock = itemView.findViewById(R.id.btnLock);
        }
    }

}