package app.spitech.ui.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.models.DataBin;
import app.spitech.appSDK.SpiTech;

/**
 * Created by Hp on 7/17/2016.
 */

public class GalleryImageViewerAdapter extends PagerAdapter {

    private LayoutInflater inflater = null;
    ArrayList<DataBin> list;
    Context context;

    public GalleryImageViewerAdapter(Context context, ArrayList<DataBin> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View page = inflater.inflate(R.layout.image_view_item, null);
        DataBin data=list.get(position);
        TextView title=page.findViewById(R.id.title);
        ImageView image=page.findViewById(R.id.image);
        title.setText(data.getTitle());
        String url= AppConfig.mediaGallery+data.getImage();
        SpiTech.getInstance().loadImage(context,url,image);

        (container).addView(page, 0);
        return page;
    }
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0== arg1;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }

}
