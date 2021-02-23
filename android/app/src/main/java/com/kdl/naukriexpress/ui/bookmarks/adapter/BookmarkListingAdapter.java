package com.kdl.naukriexpress.ui.bookmarks.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.bookmarks.Bookmarks;
import com.kdl.naukriexpress.ui.job.JobDetails;
import com.kdl.naukriexpress.ui.news.NewsDetails;


public class BookmarkListingAdapter extends RecyclerView.Adapter<BookmarkListingAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list = new ArrayList<DataBin>();
    Context context;
    Bookmarks parent;
    String type;

    // RecyclerView recyclerView;
    public BookmarkListingAdapter(Context context1, ArrayList<DataBin> list1, String type) {
        this.context = context1;
        this.list = list1;
        this.type = type;
        parent = (Bookmarks) context1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_news_seminar_listing_row, parent, false);
        return new BookmarkListingAdapter.ViewHolder(itemView);
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

        String date = SpiTech.getInstance().getMyDate("d MMM, Y", data.getDate());
        String today = parent.getDate("d MMM, Y");
        Log.e("Date", today);
        if (date.equalsIgnoreCase(today)) {
            date = "Today";
        }
        holder.date.setText(date.toUpperCase());
        holder.title.setText(data.getTitle());

        holder.time.setText(data.getTime());
        String share_text = data.getTitle();
        String url = data.getImage();
        SpiTech.getInstance().loadImage(context,url,holder.image);     ;
        holder.share.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, share_text);
            sendIntent.setType("text/plain");
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(sendIntent, "Share"));
        });
        holder.bookmarks.setOnClickListener(v -> parent.saveBookmark(context, type, String.valueOf(data.getRowId()), parent.session.getUserId()));
        holder.itemView.setOnClickListener(v -> {
            if (type.equalsIgnoreCase("news")) {
                Intent intent = new Intent(context, NewsDetails.class);
                intent.putExtra("news_id", String.valueOf(data.getRowId()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (type.equalsIgnoreCase("jobs")) {
                Intent intent = new Intent(context, JobDetails.class);
                intent.putExtra("job_id", String.valueOf(data.getRowId()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, share, bookmarks, time;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.title);
            share = itemView.findViewById(R.id.share);
            bookmarks = itemView.findViewById(R.id.bookmarks);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
        }
    }

}