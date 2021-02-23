package com.kdl.naukriexpress.ui.gallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.gallery.GalleryImage;
import com.kdl.naukriexpress.ui.home.FrgHome;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.SpiTech;

public class GalleryListAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    ArrayList<DataBin> list;
    Context context;
    FrgHome frgHome;
    AppSession session;

    public GalleryListAdapter(Context context, ArrayList<DataBin> list) {
        this.context = context;
        this.list = list;
        this.frgHome=frgHome;
        session=new AppSession(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder {
        public TextView title;
        public ImageView image;
        public LinearLayout linearLayoutView;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        final ViewHolder holder;
        if (view == null) {
            vi = inflater.inflate(R.layout.gallery_row, null);
            holder = new ViewHolder();
            holder.title = vi.findViewById(R.id.title);
            holder.image = vi.findViewById(R.id.image);
            holder.linearLayoutView= vi.findViewById(R.id.linearLayoutView);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        final DataBin data=list.get(position);

        holder.title.setText(data.getTitle());
        String url = AppConfig.mediaGallery+data.getImage();
        SpiTech.getInstance().loadImage(context,url, holder.image);
        holder.linearLayoutView.setOnClickListener(view1 -> {
            Intent intent=new Intent(context, GalleryImage.class);
            intent.putExtra("gallery",data.getTitle());
            intent.putExtra("gallery_id",data.getRowId());
            context.startActivity(intent);
        });
        return vi;
    }

}