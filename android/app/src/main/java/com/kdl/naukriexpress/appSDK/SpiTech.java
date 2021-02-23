package com.kdl.naukriexpress.appSDK;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.kdl.naukriexpress.R;

public class SpiTech extends Application implements Cloneable {
    private static volatile SpiTech spiTech;

    private SpiTech() {
        //St1ep1 - Prevent form the reflection api.
        if (spiTech != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SpiTech getInstance() {
        if (spiTech == null) {
            //St1ep2 - Prevent  singleton from thread.
            synchronized (SpiTech.class) {
                if (spiTech == null) {
                    spiTech = new SpiTech();
                }
            }
        }
        return spiTech;
    }

    //St1ep3 - Prevent  singleton from serialize and deserialize operation.
    protected SpiTech readResolve() {
        return getInstance();
    }

    //St1ep4 - Prevent  singleton from cloning.
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    /*------------------------methods-----------------*/


    public void recyclerViewHorizontalSeperator(Context context, RecyclerView recyclerView){
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(context, R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);
    }

    public void log(Context context) {
        Toast.makeText(context, "demo", Toast.LENGTH_LONG).show();
    }

    public String getTrimedString(String str, int CharaceterSize) {
        if (str.length() > CharaceterSize) {
            str = str.substring(0, CharaceterSize) + ".";
        }
        return str;
    }

    public void clearGlidCacheAndMemory(Context context){
        Glide.get(context).clearMemory();
        new Thread(() -> Glide.get(context).clearDiskCache()).start();
    }

    public boolean checkIfURLExists(String targetUrl) {
        HttpURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpURLConnection) new URL(targetUrl)
                    .openConnection();
            httpUrlConn.setRequestMethod("HEAD");
            httpUrlConn.setConnectTimeout(30000);
            httpUrlConn.setReadTimeout(30000);
            return (httpUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            Log.e("Error1:",e.getMessage());
            return false;
        }
    }


    public String getNamedPhoto(String data){
        String response="";
        try {
            response=data.substring(0,1).toUpperCase();
        }catch (Exception ex){
        }
        return response;
    }

    public void loadImage(Context context, String url, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                     .skipMemoryCache(true)
                     .placeholder(R.mipmap.ic_launcher)
                    .into(imageView);
        } catch (Exception ex) {
            Log.e("SpiTech.Java-loadImage", ex.getMessage().toLowerCase());
        }
    }

    public String getAppVersion(Context context) {
        String version = "0.0";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public void loadCustomImage(Context context, String url, ImageView imageView, int image_resource) {
        try {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                   // .skipMemoryCache(true)
                    .placeholder(image_resource)
                    .error(image_resource)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(imageView);
        } catch (Exception ex) {
            Log.e("SpiTech.Java-loadImage", ex.getMessage().toLowerCase());
        }
    }

    public void loadRoundedImage(Context context, String url, ImageView imageView, int image_resource) {
        try {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                   // .skipMemoryCache(true)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(image_resource)
                    .error(image_resource)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(imageView);
        } catch (Exception ex) {
            Log.e("SpiTech.Java-loadImage", ex.getMessage().toLowerCase());
        }
    }

    public void launchMarket(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, " Sorry, Not able to open!", Toast.LENGTH_SHORT).show();
        }
    }


    public void getUnderLine(TextView attachment, String Text) {
        String styledText = "<u><font color='blue'>" + Text + "</font></u>";
        attachment.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
    }

    public void getHyperLink(TextView attachment, String Text, String url) {
        attachment.setLinksClickable(true);
        attachment.setMovementMethod(LinkMovementMethod.getInstance());
        String styledText = "<a href='" + url + "'><u><font color='blue'>" + Text + "</font></u></a>";
        attachment.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
    }

    public String getMyDate(String format, String date_input) {
        String outputDateStr = "";
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd"); // 2016-05-29
            DateFormat outputFormat = new SimpleDateFormat(format);
            Date date = inputFormat.parse(date_input);
            outputDateStr = outputFormat.format(date);

        } catch (Exception ex) {

        }
        return outputDateStr;
    }
}
