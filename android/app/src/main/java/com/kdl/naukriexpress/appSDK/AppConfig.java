package com.kdl.naukriexpress.appSDK;

import androidx.appcompat.app.AppCompatActivity;

public class AppConfig extends AppCompatActivity {

    //-------------MODULES BEGIN----------------
    public static boolean enableOTP = true;
    public static boolean showLog = true;
    public static boolean enableFacebookLogin = true;
    public static boolean enableGmailLogin = true;
    public static boolean enableChat = true;
    public static boolean enableTodayJoinee = true;
    public static boolean enableTodayTopper = true;
    public static boolean enablePublication = true;
    public static boolean enableBatch = true;
    public static boolean enableScreenCapture = true;
    public static boolean enableDailyQuiz = true;
    //------------------MODULES END--------------------------

    //--------Client Details Begin---------------
    public static String domain_name="app.naukriexpress.net";
    public static String playstore_package="com.kdl.naukriexpress";
    public static String sdcard_folder="naukriexpress.net";

    public static String adminMobile="7828796979";
    public static String appWhatsAppContact="7828796979";
    public static String appName = "SpiTech Academy";
    public static String api_key = "a6d1ff65b2144383cd737dc55f9129cf";
    //--------Client Details End---------------



    public static String appFolderName=sdcard_folder+"/data/";
    public static String appFolderPath = "/sdcard/"+appFolderName; // use this for checking isExist or not

    public static String waterMarkForImageSharing = appName;
    public static String fcmTopic = "AppSubscriber";
    //public static String server="http://192.168.43.96/spitech/products/exam-portal/";
    public static String server = "http://" + domain_name + "/";
    public static String academyMediaFolder = server + "media/" + domain_name + "/";
    public static String appLink = "https://play.google.com/store/apps/details?id=" + playstore_package;
    public static String fileProvider=playstore_package+".fileProvider";


    public static String sharedApi, authApi, productApi, orderApi, testApi, userApi, messageApi, moduleApi, batchApi;
    public static String mediaBanner;
    public static String mediaCustomer;
    public static String mediaProduct;
    public static String mediaNotification;
    public static String mediaJob;
    public static String mediaBlog;
    public static String mediaDownloads;
    public static String mediaGallery;
    public static String mediaGroup;
    public static String mediaPDF;

    void initializeAcademy() {
        //----apis--------------
        batchApi = server + "batch/";
        sharedApi = server + "shared/";
        authApi = server + "auth/";
        productApi = server + "product/";
        orderApi = server + "order/";
        testApi = server + "test/";
        userApi = server + "user/";
        messageApi = server + "message/";
        moduleApi = server + "module/";
        //----media--------------
        mediaBanner = academyMediaFolder + "banner/";
        mediaCustomer = academyMediaFolder + "customer/";
        mediaProduct = academyMediaFolder + "packages/";
        mediaNotification = academyMediaFolder + "notification/";
        mediaGroup = academyMediaFolder + "group/";
        mediaJob = academyMediaFolder + "job/";
        mediaBlog = academyMediaFolder + "blogs/";
        mediaDownloads = academyMediaFolder + "downloads/";
        mediaGallery = academyMediaFolder + "gallery/";
        mediaPDF = academyMediaFolder + "pdf/";

    }

}
