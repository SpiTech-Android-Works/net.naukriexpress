package com.kdl.naukriexpress.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.CustomUI;
import com.kdl.naukriexpress.appSDK.Interfaces.CommonInterface;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.job.JobDetails;
import com.kdl.naukriexpress.ui.news.NewsDetails;

import java.util.ArrayList;


public class CommonJNSASListingAdapter extends RecyclerView.Adapter<CommonJNSASListingAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    CustomUI customUI;
    AppSession session;
    String type;

    // RecyclerView recyclerView;
    public CommonJNSASListingAdapter(Context context1, ArrayList<DataBin> list1, String type) {
        this.context = context1;
        this.list = list1;
        this.type = type;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customUI = new CustomUI();
        session = new AppSession(this.context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_news_seminar_listing_row, parent, false);
        return new CommonJNSASListingAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        if (data.getIsBookmarked() == 1) {
            holder.bookmarks.setTextColor(Color.BLUE);
            holder.bookmarks.setText("Bookmarked");
        } else {
            holder.bookmarks.setText("Bookmark");
        }

        String date = customUI.getMyDate("d MMM, Y", data.getDate());
        String today = customUI.getDate("d MMM, Y");
        Log.e("Date", today);
        if (date.equalsIgnoreCase(today)) {
            date = "Today";
        }
        holder.date.setText(date.toUpperCase());
        holder.title.setText(data.getTitle());
        holder.time.setText(data.getTime());
        holder.view_counter.setText(data.getViewCounter()+" Views");
        String url = data.getImage();
        SpiTech.getInstance().loadImage(context,url,holder.image);
        holder.share.setOnClickListener(v -> shareDetails(data));
        holder.bookmarks.setOnClickListener(v -> {
            if (context instanceof CommonInterface) {
                ((CommonInterface) context).saveBookmark(context, type, String.valueOf(data.getRowId()), session.getUserId());
            }
        });
        holder.itemView.setOnClickListener(v -> viewDetails(data));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, share, bookmarks, time,view_counter;
        public LinearLayout linearLayoutView;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.title);
            share = itemView.findViewById(R.id.share);
            bookmarks = itemView.findViewById(R.id.bookmarks);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
            view_counter = itemView.findViewById(R.id.view_counter);
        }
    }

    void shareDetails(DataBin data){
        String msg=data.getTitle();
        msg+="\n"+context.getResources().getString(R.string.link_app);
        customUI.share(context,msg);
    }

    void viewDetails(DataBin data){
        if (type.equalsIgnoreCase("news")) {
            Intent intent = new Intent(context, NewsDetails.class);
            intent.putExtra("news_id", String.valueOf(data.getRowId()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else  if (type.equalsIgnoreCase("jobs")) {
            Intent intent = new Intent(context, JobDetails.class);
            intent.putExtra("job_id", String.valueOf(data.getRowId()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}