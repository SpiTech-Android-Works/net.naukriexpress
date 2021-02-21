package app.spitech.appSDK;

//port android.app;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import app.spitech.appSDK.Interfaces.CommonInterface;
import app.spitech.models.DataBin;

/**
 * Created by in.gdc4gpatnew.spitech on 12/29/17.
 */

public class BaseFragment extends Fragment implements CommonInterface {

    public String tag = "";
    public AppSession session;
    public static Context mContext;
    public ProgressBar progressBar;

    @Override
    public void saveBookmark(Context context, String type, String primary_key, String customer_id) {

    }

    @Override
    public void onAdapterItemClick(String[] args) {

    }

    @Override
    public void onAdapterItemClick(DataBin dataBin) {

    }

    @Override
    public void buyCourse() {
        Log.e("buyCourse","Called");
    }
}
