package app.spitech.appSDK.Interfaces;

import android.content.Context;

import app.spitech.models.DataBin;

public interface CommonInterface {
    void saveBookmark(Context context, String type, String primary_key, String customer_id);
    void onAdapterItemClick(String args[]);
    void onAdapterItemClick(DataBin dataBin);
    void buyCourse();
}
