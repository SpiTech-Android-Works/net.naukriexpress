package com.kdl.naukriexpress.appSDK.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.Interfaces.RetrofitInterface;
import com.kdl.naukriexpress.ui.publication.Publication;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FileDownloadService extends IntentService {
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public FileDownloadService() {
        super("Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setDescription("no sound");
                notificationChannel.setSound(null, null);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationBuilder = new NotificationCompat.Builder(this, "id")
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle("Download")
                    .setContentText(intent.getExtras().getString("msg"))
                    .setDefaults(0)
                    .setAutoCancel(true);
            notificationManager.notify(0, notificationBuilder.build());
            initRetrofit(intent.getExtras().getString("media_folder_url"),intent.getExtras().getString("file_name"));
        } else {
            Log.e("file_name1", "Intent not passing to service");
        }
    }


    private void initRetrofit(String media_folder_url,String file_name) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(media_folder_url)
                .build();
        //Log.e();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Log.e("file_name1", file_name);
        Call<ResponseBody> request = retrofitInterface.downloadImage(file_name);
        try {
            downloadImage(request.execute().body(), file_name);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("initRetrofit",e.getMessage());
            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadImage(ResponseBody body, String file_name) {

        try {
            int count;
            byte data[] = new byte[1024 * 4];
            long fileSize = body.contentLength();
            InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);

            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+ AppConfig.appFolderName;
            File newDir = new File(directory);
            newDir.mkdirs();
            directory+=File.separator;
            String path = directory + file_name;

            Log.e("FileLocation",path);

            File outputFile = new File(path);
            OutputStream outputStream = new FileOutputStream(outputFile);
            long total = 0;
            boolean downloadComplete = false;
            while ((count = inputStream.read(data)) != -1) {
                total += count;
                int progress = (int) ((double) (total * 100) / (double) fileSize);
                updateNotification(progress);
                outputStream.write(data, 0, count);
                downloadComplete = true;
            }
            onDownloadComplete(downloadComplete, file_name);
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
        }
    }

    private void updateNotification(int currentProgress) {
        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendProgressUpdate(boolean downloadComplete, String file_name) {
        //Intent intent = new Intent(FrgJobDetails.PROGRESS_UPDATE);
        Intent intent = new Intent(Publication.PROGRESS_UPDATE);
        intent.putExtra("downloadComplete", downloadComplete);
        intent.putExtra("file_name", file_name);
        LocalBroadcastManager.getInstance(FileDownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete(boolean downloadComplete, String file_name) {
        sendProgressUpdate(downloadComplete, file_name);
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Image Download Complete");
        notificationManager.notify(0, notificationBuilder.build());
        notificationManager.cancelAll();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancelAll();
    }


}
