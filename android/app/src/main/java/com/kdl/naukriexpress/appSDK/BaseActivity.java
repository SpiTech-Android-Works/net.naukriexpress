package com.kdl.naukriexpress.appSDK;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.ui.other.ExitActivity;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends HelperMethods {

    public FirebaseUser currentUser;
    public FirebaseAuth mAuth;
    public DatabaseReference mDB;

    public List<Integer> aHideMenuList = new ArrayList<>();
    public View screen;
    public LinearLayout emptyView;
    public TextView emptyTextView;
    public ProgressBar progressBar;
    public Toolbar toolbar;
    public TextView toolbar_title;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (aHideMenuList != null) {
            MenuItem item = null;
            for (int menuId : aHideMenuList) {
                item = menu.findItem(menuId);
                item.setVisible(false);
            }
        }
        if (screen != null) {
            menu.findItem(R.id.action_share).setVisible(true);
        } else {
            menu.findItem(R.id.action_share).setVisible(false);
        }
        return true;
    }



    public void showLog(String tag, String error){
        if(AppConfig.showLog){
            Log.e(tag, error);
        }
    }

    public void showData(View emptyView, View dataContainer, int showData) {
        try {
            if (showData == 1) {
                emptyView.setVisibility(View.GONE);
                dataContainer.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                dataContainer.setVisibility(View.GONE);
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            String shareBody = getResources().getString(R.string.link_app);
            screanSharing(BaseActivity.this, screen, shareBody);
        } else if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void backToClose() {
        new AlertDialog.Builder(BaseActivity.this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Confirmation?")
                .setMessage("Do you want to exit form this App?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    ExitActivity.exit(BaseActivity.this);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public void logout() {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(BaseActivity.this);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setTitle("Confirmation?");
        alertDialog.setMessage("Are you sure want to logout?");
        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    AppSession session = new AppSession(BaseActivity.this);
                    session.logoutUser();
                });
        alertDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        alertDialog.create();
        if(!isFinishing()){
            alertDialog.show();
        }
    }

    private static final int REQUEST_READ_PHONE_STATE=110;
    String mDeviceIMESI="";
    public String getDeviceIMEI1(){
       try{
           int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
           if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
           } else {
               TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
               this.mDeviceIMESI = TelephonyMgr.getDeviceId();
           }
       }catch (Exception ex){
           mDeviceIMESI="";
       }
        return mDeviceIMESI;
    }

    public String getDeviceId(Context context){
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("DeviceId",android_id);
        return android_id;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
                    this.mDeviceIMESI = TelephonyMgr.getDeviceId();
                }
                break;
            default:
                break;
        }
    }

}
