package com.kdl.naukriexpress.appSDK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.adapter.DialogListAdapter;
import com.kdl.naukriexpress.appSDK.Interfaces.CommonInterface;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.auth.Login;
import com.kdl.naukriexpress.ui.other.NetworkNotExist;

public class HelperMethods extends AppConfig implements CommonInterface {

    public String tag = "";
    public Context context;
    public AppSession session;
    public ProgressDialog progress;
    public FragmentTransaction fragmentTransaction;
    public boolean checkLogin = true;
    public boolean checkInternet = true;

    public void load(Context context1, String tag, String title) {
        AppConfig obj = new AppConfig();
        obj.initializeAcademy();
        this.context = context1;
        this.tag = tag;
        if (title.equalsIgnoreCase("default")) {
            setTitle(getResources().getString(R.string.company_name).toUpperCase());
        } else {
            setTitle(title);
        }
        session = new AppSession(context);
        if (checkLogin == true && !session.isUserLoggedIn()) {
            startActivity(new Intent(context1, Login.class));
        }
        AppMethods.getInstance().checkAppStatus(HelperMethods.this);
        if(checkInternet){
            if (!StaticMethods.isNetworkAvailable(HelperMethods.this)) {
                startActivity(new Intent(context1, NetworkNotExist.class));
            }
        }

    }


    //-------------Permission Begin----------------
    private int PERMISSION_ID = 42;
    public boolean checkFilePermissions()  {
        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        }
        return false;
    }
    protected void requestFilePermissions() {
        String [] aPermission= {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(
                this,
                aPermission,
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            }
        }
    }

    //-----Permission end-------------


    public void readyOnlyEditText(EditText edt) {
        edt.setClickable(true);
        edt.setFocusable(false);
        edt.setCursorVisible(false);
        edt.setKeyListener(null);
        edt.setShowSoftInputOnFocus(false);
    }

    public void stopScreenshot() {
        if (enableScreenCapture == false) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    public void debug(String tag, String msg) {
        Log.e(tag, msg);
    }


    public static String getMoneyFormat(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String moneyString = formatter.format(amount);
        return moneyString;
    }

    void showSnakBar(View view, String msg, String action) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction(action, null).show();
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

    public static String getDate(String date_fromat) {
        String res = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(date_fromat);  // dd-MMMM-yyyy
            Date date = new Date();
            res = dateFormat.format(date).toString();
        } catch (Exception ex) {
        }
        return res;
    }


    AlertDialog.Builder builder;

    public void showConfirm(String title, String msg) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> Toast.makeText(getApplicationContext(), "Yes",
                Toast.LENGTH_SHORT).show());
        builder.setNegativeButton("No", (dialog, which) -> Toast.makeText(getApplicationContext(), "NO",
                Toast.LENGTH_SHORT).show());
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(title);
        if(!isFinishing()){
            alertDialog.show();
        }
    }

    public void showAlert(String msg) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(AppConfig.appName);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        if(!isFinishing()){
            alertDialog.show();
        }
    }

    public void showProgress(Context ctx, String msg) {
        try {
            if (this.progress != null) {
                this.progress.dismiss();
            }
            this.progress = new ProgressDialog(ctx);
            this.progress.setMessage(msg);
            this.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progress.setIndeterminate(true);
            this.progress.setCanceledOnTouchOutside(false);
            if(!isFinishing()){
                this.progress.show();
            }
        } catch (Exception ex) {

        }
    }

    public void closeProgress() {
        if (!isFinishing() && this.progress != null && this.progress.isShowing()) {
            this.progress.dismiss();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public void openUrl(String url) {
        try {
            Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception ex) {
            showAlert("Url is either empty or invalid");
        }
    }

    public void contactWhatsapp(String number, String msg) {
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            String url = "https://api.whatsapp.com/send?phone=+91-" + number + "&text=" + msg;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else {
            Toast.makeText(this, "WhatsApp not installed",
                    Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
        }
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void switchFragment(Fragment fragment) {
        try {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception ex) {
            //showAlert("switchFragment"+ex.getMessage());
        }

    }

    public String setStringValue(String str) {
        String result = "";
        if (str != null && !str.trim().equalsIgnoreCase("") && !str.trim().equalsIgnoreCase("null")) {
            result = str;
        }
        return result;
    }

    public Bitmap waterMarking(Bitmap bitmap, String watermark) {
        //-----WATERMARKING START-----------
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setAlpha(70);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(100);
        canvas.rotate(-45);
        float xOffset = bitmap.getWidth() / 2;
        float yOffset = bitmap.getHeight() / 2;
        canvas.drawText(watermark, -(xOffset + 100), yOffset + 100, paint);
        //-----WATERMARKING STOP-----------
        return bitmap;
    }

    //--------------------------sharing screen-------------------------
    public void screanSharing(Context context, View screenView, String shareBody) {

        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {

            try {
                //-----------Step1 taking screenshot and creating bitmap-------------
                screenView.setBackgroundColor(Color.WHITE);
                screenView.invalidate();
                screenView.setDrawingCacheEnabled(true);
                screenView.buildDrawingCache(true);
                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
                bitmap = waterMarking(bitmap, AppConfig.waterMarkForImageSharing);
                screenView.setDrawingCacheEnabled(false);

                //-----------Step2 saving bitmap-------------
                Random r = new Random();
                int i1 = r.nextInt(1000 - 0) + 100;
                String currentDateAndTime = new SimpleDateFormat("ddMMyyHHmmss").format(new Date());
                String fileName = i1 + currentDateAndTime + ".png";
                String file_path = Environment.getExternalStorageDirectory() + "/" + fileName;

                File file = new File(file_path);

                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();

                screenView.destroyDrawingCache();
                screenView.setBackground(null);

                //-----------Step3 sharing screenshot-------------
                //Uri uri = Uri.fromFile(file);
                Uri uri = FileProvider.getUriForFile(context, AppConfig.fileProvider, file);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                //---deleting file_provider_paths from sd card------
                File fdelete = new File(uri.getPath());
                if (fdelete.exists()) {
                    fdelete.delete();
                    Log.e("ShareAction", "file_provider_paths Deleted :" + uri.getPath());
                }

            } catch (Exception ex) {
                Log.e("SpiTechScreanSharing", ex.getMessage());
            }
        }

    }

    public void share(Context context, String shareBody) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void whatsappShare(Context context, String msg) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, msg);
        whatsappIntent.putExtra(Intent.EXTRA_TITLE, AppConfig.appName);
        try {
            context.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("Whatsapp have not been installed.");
        }
    }

    public void openFile(Context context, String fileName) {

        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + AppConfig.appFolderName + File.separator;
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
        if (file.getAbsoluteFile().exists() == true) {
            intent = Intent.createChooser(intent, "Open File");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            try {
                startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_LONG).show();
                Log.e("Exception", "openPDF()" + ex.getMessage());
            }
        } else {
            Toast.makeText(getApplicationContext(), "File path is incorrect.", Toast.LENGTH_LONG).show();
        }
    }

    public void showError(EditText editText, String msg) {
        editText.requestFocus();
        editText.setError(msg);
        showAlert(msg);
    }


    public void saveBookmark(Context context, String type, String primary_key, String customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "save_bookmark",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("data");
                        showToast(msg);
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("type", type);
                params.put("primary_key", primary_key);
                params.put("customer_id", customer_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAdapterItemClick(String[] args) {
        Log.e("onAdapterItemClick", "Clicked");
    }

    @Override
    public void onAdapterItemClick(DataBin args) {
        Log.e("onAdapterItemClick", "Clicked");
    }

    @Override
    public void buyCourse() {
        Log.e("buyCourse","Called");
    }


    public void stateDialog(Activity activity, String dialogTitle, EditText editText, TextView hiddenFieldId) {
        String tag = "state_list";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_list, null);
        builder.setView(customView);
        builder.setTitle(dialogTitle);
        RecyclerView recyclerView = customView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        EditText inputSearch = customView.findViewById(R.id.inputSearch);
        AlertDialog dialog = builder.create();


        DialogListAdapter adapter =new DialogListAdapter(activity, dialog, editText, hiddenFieldId);
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "state_list",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("status").equalsIgnoreCase("1")){
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("state_id"));
                                data.setName(value.getString("state"));
                                adapter.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("country_id","88");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                adapter.getFilter(cs.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (!activity.isFinishing()) {
            dialog.show();
        }
    }

    public void countryDialog(Activity activity, String dialogTitle, EditText editText, TextView hiddenFieldId) {
        String tag = "country_list";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_list, null);
        builder.setView(customView);
        builder.setTitle(dialogTitle);
        RecyclerView recyclerView = customView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        EditText inputSearch = customView.findViewById(R.id.inputSearch);
        AlertDialog dialog = builder.create();


        DialogListAdapter adapter =new DialogListAdapter(activity, dialog, editText, hiddenFieldId);
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "country_list",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("status").equalsIgnoreCase("1")){
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("country_id"));
                                data.setTitle(value.getString("country"));
                                adapter.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                adapter.getFilter(cs.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (!activity.isFinishing()) {
            dialog.show();
        }
    }
}

