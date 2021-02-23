package com.kdl.naukriexpress.appSDK.Interfaces;

import android.content.Context;

import com.kdl.naukriexpress.models.DataBin;

public interface CommonInterface {
    void saveBookmark(Context context, String type, String primary_key, String customer_id);
    void onAdapterItemClick(String args[]);
    void onAdapterItemClick(DataBin dataBin);
    void buyCourse();
}
