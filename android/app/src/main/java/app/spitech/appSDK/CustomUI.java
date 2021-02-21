package app.spitech.appSDK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import app.spitech.R;

public class CustomUI extends HelperMethods {

    public String tag = "";
    public Context context;
    public AppSession session;
    public ProgressDialog progress;
    public FragmentTransaction fragmentTransaction;
    AlertDialog.Builder builder;

    public static String getMoneyFormat(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String moneyString = formatter.format(amount);
        return moneyString;
    }

    public static String getRs(double amount) {
        String rs = "\u20B9";
        try {
            rs = "\u20B9";
            byte[] utf8 = rs.getBytes("UTF-8");
            rs = new String(utf8, "UTF-8");
            rs = getMoneyFormat(amount).replace("Rs.", rs);
        } catch (Exception ex) {
            rs = "00.00";
        }
        return rs;
    }

    public static void ratings(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, " Sorry, Not able to open!", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap mergeBitmap(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap mergedBitmap = null;
        int w, h = 0;
        h = bitmap1.getHeight() + bitmap2.getHeight();
        if (bitmap1.getWidth() > bitmap2.getWidth()) {
            w = bitmap1.getWidth();
        } else {
            w = bitmap2.getWidth();
        }
        mergedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mergedBitmap);
        canvas.drawBitmap(bitmap1, 0f, 0f, null);
        canvas.drawBitmap(bitmap2, 0f, bitmap1.getHeight(), null);
        return mergedBitmap;
    }

    public void load(Context context1, String tag, String title) {
        this.context = context1;
        this.tag = tag;
        if (title.equalsIgnoreCase("default")) {
            setTitle(getResources().getString(R.string.company_name).toUpperCase());
        } else {
            setTitle(title);
        }
        session = new AppSession(context);
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

    public void showConfirm(String title, String msg) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Yes",
                        Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "NO",
                        Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.show();
    }

    public void showAlert(String title, String msg) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.show();
    }

    public void showProgress(Context ctx, String msg) {
        if (this.progress != null) {
            this.progress.dismiss();
        }
        this.progress = new ProgressDialog(ctx);
        this.progress.setMessage(msg);
        this.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progress.setIndeterminate(true);
        this.progress.setCanceledOnTouchOutside(false);
        this.progress.show();
    }

    public void closeProgress() {
        if (this.progress != null && this.progress.isShowing()) {
            this.progress.dismiss();
        }
    }


    public void switchFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void showSnack(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public void openUrl(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void contactWhatsapp(String number) {
        String url = "https://api.whatsapp.com/send?phone=+91-" + number;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public String setStringValue(String str) {
        String result = "";
        if (str != null && !str.trim().equalsIgnoreCase("") && !str.trim().equalsIgnoreCase("null")) {
            result = str;
        }
        return result;
    }

    public void selectValue(Spinner spinner, Object value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public Bitmap waterMarking(Context context, Bitmap bitmap, String watermark) {
        //-----WATERMARKING START-----------
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setAlpha(40);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(90);
        canvas.rotate(-40);
        Rect rectText = new Rect();
        paint.getTextBounds(watermark, 0, watermark.length(), rectText);
        int yOffset = bitmap.getHeight() / 2;
        canvas.drawText(watermark, 0, 500, paint);
        //-----WATERMARKING STOP-----------
        return bitmap;
    }

    //--------------------------sharing screen-------------------------
    public void screanSharing(Context context, View rootView, String subject, String shareBody) {

        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }else {
            //-----------Step1 taking screenshot and creating bitmap-------------
            //View screenView = rootView.getRootView();
            View screenView = rootView;
            screenView.setBackgroundColor(Color.WHITE);
            screenView.invalidate();
            screenView.setDrawingCacheEnabled(true);
            screenView.buildDrawingCache(true);
            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
            bitmap = waterMarking(context, bitmap, AppConfig.waterMarkForImageSharing);
            rootView.setDrawingCacheEnabled(false);

            //-----------Step2 saving bitmap-------------
            Random r = new Random();
            int i1 = r.nextInt(1000 - 0) + 100;
            String currentDateAndTime = new SimpleDateFormat("ddMMyyHHmmss").format(new Date());
            String fileName = i1 + currentDateAndTime + ".png";
            String file_path = Environment.getExternalStorageDirectory() + "/" + fileName;

            File file = new File(file_path);
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            screenView.destroyDrawingCache();
            screenView.setBackground(null);

            //-----------Step3 sharing screenshot-------------
            //Uri uri = Uri.fromFile(file);
            Uri uri = FileProvider.getUriForFile(context, AppConfig.fileProvider, file);
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

            //---deleting file_provider_paths from sd card------
            File fdelete = new File(uri.getPath());
            if (fdelete.exists()) {
                Log.e("ShareAction", "file_provider_paths Deleted :" + uri.getPath());
            }
        }

    }

    public void share(Context context, String shareBody) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        //sharingIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void whatsappShare(Context context, String msg) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, msg);
        try {
            context.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("Whatsapp have not been installed.");
        }
    }

    public void openFile(Context context, String fileName) {
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AppConfig.appFolderName + File.separator;
        String filePath = directory + fileName;

        File file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Log.e("filePath", filePath);
        Uri uri = FileProvider.getUriForFile(context, AppConfig.fileProvider, file);
        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            if (fileName.contains(".doc") || fileName.contains(".docx")) {
                intent.setDataAndType(Uri.parse("content:///" + filePath), "application/msword");

            } else if (fileName.contains(".pdf")) {
                intent.setDataAndType(Uri.parse("content:///" + filePath), "application/pdf");

            } else if (fileName.contains(".ppt") || fileName.contains(".pptx")) {
                intent.setDataAndType(Uri.parse("content:///" + filePath), "application/vnd.ms-powerpoint");

            } else if (fileName.contains(".xls") || fileName.contains(".xlsx")) {

                intent.setDataAndType(Uri.parse("content:///" + filePath), "application/vnd.ms-excel");
            } else if (fileName.contains(".zip")) {

                intent.setDataAndType(Uri.parse("content:///" + filePath), "application/zip");
            } else if (fileName.contains(".rar")) {

                intent.setDataAndType(Uri.parse("content:///" + filePath), "application/x-rar-compressed");
            } else if (fileName.contains(".rtf")) {

                intent.setDataAndType(Uri.parse("content:///" + filePath), "application/rtf");
            } else if (fileName.contains(".wav") || fileName.contains(".mp3")) {

                intent.setDataAndType(Uri.parse(directory + fileName), "audio/x-wav");
            } else if (fileName.contains(".gif")) {

                intent.setDataAndType(Uri.parse("content:///" + filePath), "image/gif");
            } else if (fileName.contains(".jpg") || fileName.contains(".jpeg") || fileName.contains(".png")) {

                intent.setDataAndType(Uri.parse("content:///" + filePath), "image/jpeg");
            } else if (fileName.contains(".txt")) {

                intent.setDataAndType(Uri.parse("content:///" + filePath), "text/plain");
            } else if (fileName.contains(".3gp") || fileName.contains(".mpg") ||
                    fileName.contains(".mpeg") || fileName.contains(".mpe") || fileName.contains(".mp4") || fileName.toString().contains(".avi")) {

                intent.setDataAndType(Uri.parse("content:///" + filePath), "video/*");
            } else {
                intent.setDataAndType(Uri.parse("content:///" + filePath), "*/*");
            }
        }
        if (file.exists()) {
            intent = Intent.createChooser(intent, "Open File");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            try {
                startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_LONG).show();
                Log.e("Exception", "openPDF()" + ex.getMessage().toString());
            }
        } else {
            Toast.makeText(getApplicationContext(), "File path is incorrect.", Toast.LENGTH_LONG).show();
        }
    }


}

