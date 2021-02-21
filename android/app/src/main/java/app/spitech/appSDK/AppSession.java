package app.spitech.appSDK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import app.spitech.ui.auth.Login;


public class AppSession {

    private static final String PREFER_NAME = "SpiTechPreference", IS_USER_LOGIN = "IsUserLoggedIn",
            KEY_ID = "id", KEY_MOBILE = "mobile", KEY_NAME = "name", KEY_COURSE = "course_id", KEY_PHOTO = "default"
            , KEY_EMAIL = "KEY_EMAIL",ACTIVE_FRAGMENT="ACTIVE_FRAGMENT",TOKEN="TOKEN",KEY_CODE="KEY_CODE",
    KEY_CURRENT_VIDEO_URL="KEY_CURRENT_VIDEO_URL",KEY_CURRENT_VIDEO_ID="KEY_CURRENT_VIDEO_ID";

    SharedPreferences pref;
    Editor editor;
    Context mContext;
    int PRIVATE_MODE = 0;

    public AppSession(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setKeyCurrentVideoUrl(String code) {
        editor.putString(KEY_CURRENT_VIDEO_URL, code);
        editor.commit();
    }
    public String getKeyCurrentVideoUrl() {
        return pref.getString(KEY_CURRENT_VIDEO_URL, null);
    }

    public void setKeyCurrentVideoId(String code) {
        editor.putString(KEY_CURRENT_VIDEO_ID, code);
        editor.commit();
    }
    public String getKeyCurrentVideoId() {
        return pref.getString(KEY_CURRENT_VIDEO_ID, null);
    }

    public void setLogin(boolean vale) {
        editor.putBoolean(IS_USER_LOGIN, vale);
        editor.commit();
    }

    public boolean checkLogin() {
        if (this.isUserLoggedIn()) {
            return true;
        } else {
            return false;
        }
    }

    public String getCode() {
        return pref.getString(KEY_CODE, null);
    }

    public void setCode(String code) {
        editor.putString(KEY_CODE, code);
        editor.commit();
    }


    public String getToken() {
        return pref.getString(TOKEN, null);
    }

    public void setToken(String token) {
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public String getMobile() {
        return pref.getString(KEY_MOBILE, null);
    }

    public void setMobile(String mobile) {
        editor.putString(KEY_MOBILE, mobile);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(KEY_ID, null);
    }

    public void setUserId(String userid) {
        editor.putString(KEY_ID, userid);
        editor.commit();
    }

    public String getName() {
        return pref.getString(KEY_NAME, null);
    }

    public void setName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getPhoto() {
        return pref.getString(KEY_PHOTO, null);
    }

    public void setPhoto(String photo) {
        editor.putString(KEY_PHOTO, photo);
        editor.commit();
    }

    public String getCourseId() {
        return pref.getString(KEY_COURSE, null);
    }

    public void setCourseId(String courseId) {
        editor.putString(KEY_COURSE, courseId);
        editor.commit();
    }


    public String getCategoryId() {
        return pref.getString("categoryId", null);
    }

    public void setCategoryId(String categoryId) {
        editor.putString("categoryId", categoryId);
        editor.commit();
    }

    public String getResultId() {
        return pref.getString("resultId", null);
    }

    public void setResultId(String resultId) {
        editor.putString("resultId", resultId);
        editor.commit();
    }

    public String getPackageId() {
        return pref.getString("packageId", null);
    }

    public void setPackageId(String packageId) {
        editor.putString("packageId", packageId);
        editor.commit();
    }

    public String getTestId() {
        return pref.getString("testId", null);
    }

    public void setTestId(String testId) {
        editor.putString("testId", testId);
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public void logoutUser() {
        pref = mContext.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(mContext, Login.class);
        intent.putExtra("logout","Yes");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    public void setActiveFragment(String activeFragment) {
        editor.putString(ACTIVE_FRAGMENT, activeFragment);
        editor.commit();
        editor.apply();
    }
    public String getActiveFragment(){
        return  pref.getString(ACTIVE_FRAGMENT, null);
    }

    public void setExamName(String courseId) {
        editor.putString(KEY_COURSE, courseId);
        editor.commit();
        editor.apply();
    }
    public String getExamName(){
        return  pref.getString(KEY_COURSE, null);
    }

    public String getPurpose() {
        return pref.getString("purpose", null);
    }

    public void setPurpose(String purpose) {
        editor.putString("purpose", purpose);
        editor.commit();
    }

    public String getSubjectId() {
        return pref.getString("subject_id", null);
    }

    public void setSubjectId(String subject_id) {
        editor.putString("subject_id", subject_id);
        editor.commit();
    }

    public String getSubject() {
        return pref.getString("subject", null);
    }

    public void setSubject(String subject) {
        editor.putString("subject", subject);
        editor.commit();
    }

    //----------chat-----------------
    public void addNotification(String notification) {
        // get old notifications
        String oldNotifications = getNotifications();
        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }
        editor.putString("notification", oldNotifications);
        editor.commit();
    }
    public String getNotifications() {
        return pref.getString("notification", null);
    }
    public void clear() {
        editor.clear();
        editor.commit();
    }

}