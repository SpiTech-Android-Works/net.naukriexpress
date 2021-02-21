package app.spitech.ui.subject.adapter;

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

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.models.DataBin;
import app.spitech.ui.home.FrgHome;
import app.spitech.ui.tricks.MnemonicsListing;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.SpiTech;

public class SubjectAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    ArrayList<DataBin> list;
    Context context;
    FrgHome frgHome;
    AppSession session;

    public SubjectAdapter(Context context, ArrayList<DataBin> list) {
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
            vi = inflater.inflate(R.layout.subject_row, null);
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
        String url = AppConfig.mediaProduct+data.getImage();
        SpiTech.getInstance().loadImage(context,url, holder.image);
        holder.linearLayoutView.setOnClickListener(view1 -> {
            session.setSubjectId(data.getRowId());
            session.setSubject(data.getTitle());
            context.startActivity(new Intent(context, MnemonicsListing.class));
        });
        return vi;
    }

}