package app.spitech.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glide.slider.library.SliderLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppMethods;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.BaseFragment;
import app.spitech.models.VideoModel;
import app.spitech.ui.branch.BranchListing;
import app.spitech.ui.daily_quiz.DailyQuiz;
import app.spitech.ui.downloads.Exam;
import app.spitech.ui.gallery.Gallery;
import app.spitech.ui.help.Help;
import app.spitech.ui.job.JobListing;
import app.spitech.ui.news.NewsListing;
import app.spitech.ui.toppers.TodaysAllToppers;
import app.spitech.ui.video.adapter.VideoAdapter;
import cz.msebera.android.httpclient.Header;


public class FrgHome extends BaseFragment implements View.OnClickListener{

    private RecyclerView recyclerViewRecentlyJoined,recyclerViewToppers;
    private LinearLayout recentlyContainerView,toppersContainerView,appUpdatesContainer,publicationContainer,videoContainer;
    private TextView btnUpdate,btnAllToppers;
    SliderLayout packageSlider;
    VideoAdapter adapterVideo;
    ArrayList<VideoModel> videoList;
    Home parent;
    AppSession session;

    private LinearLayout connectYoutube, connectInsta, connectFacebook, connectTelegram, connectTwitter, connectWhatsApp, connectEmail, connectContactUs,connectHelp;
    private LinearLayout job, news,  downloads, videos, gallery, address,batchContainer,packageContainer;
    private LinearLayout   box_daily_quiz;
    private RecyclerView recyclerViewVideo;
    private RecyclerView recyclerBooks,recyclerViewBatch;

    public FrgHome() {

    }

    public static FrgHome newInstance() {
        return new FrgHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //----------Check App Update Begin-------------
        appUpdatesContainer=rootView.findViewById(R.id.appUpdatesContainer);
        btnUpdate=rootView.findViewById(R.id.btnUpdate);
        AppMethods.getInstance().checkAppVersion(getContext(), appUpdatesContainer);

        btnUpdate.setOnClickListener(v -> parent.openUrl(AppConfig.appLink));
        //----------Check App Update End-------------

        videoContainer=rootView.findViewById(R.id.videoContainer);
        recyclerViewVideo =  rootView.findViewById(R.id.recyclerViewVideos);
        recyclerViewVideo.setHasFixedSize(true);
        recyclerViewVideo.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        publicationContainer=rootView.findViewById(R.id.publicationContainer);
        recyclerBooks = rootView.findViewById(R.id.recyclerBooks);
        recyclerBooks.setHasFixedSize(true);
        recyclerBooks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        batchContainer=rootView.findViewById(R.id.batchContainer);
        recyclerViewBatch = rootView.findViewById(R.id.recyclerViewBatch);
        recyclerViewBatch.setHasFixedSize(true);
        recyclerViewBatch.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        packageContainer=rootView.findViewById(R.id.packageContainer);
        packageSlider=rootView.findViewById(R.id.packageSlider);

        job = rootView.findViewById(R.id.job);
        news = rootView.findViewById(R.id.news);
        downloads = rootView.findViewById(R.id.downloads);
        videos = rootView.findViewById(R.id.videos);
        gallery = rootView.findViewById(R.id.gallery);
        address = rootView.findViewById(R.id.address);

        connectYoutube = rootView.findViewById(R.id.connectYoutube);
        connectInsta = rootView.findViewById(R.id.connectInsta);
        connectFacebook = rootView.findViewById(R.id.connectFacebook);
        connectTelegram = rootView.findViewById(R.id.connectTelegram);
        connectTwitter = rootView.findViewById(R.id.connectTwitter);
        connectWhatsApp = rootView.findViewById(R.id.connectWhatsApp);
        connectEmail = rootView.findViewById(R.id.connectEmail);
        connectContactUs = rootView.findViewById(R.id.connectContactUs);
        connectHelp= rootView.findViewById(R.id.connectHelp);
        btnAllToppers= rootView.findViewById(R.id.btnAllToppers);

        box_daily_quiz = rootView.findViewById(R.id.box_daily_quiz);

        recentlyContainerView=rootView.findViewById(R.id.recentlyContainerView);
        recyclerViewRecentlyJoined =  rootView.findViewById(R.id.recyclerViewRecentlyJoined);
        recyclerViewRecentlyJoined.setHasFixedSize(true);
        recyclerViewRecentlyJoined.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        toppersContainerView=rootView.findViewById(R.id.toppersContainerView);
        toppersContainerView.setNestedScrollingEnabled(false);
        recyclerViewToppers = rootView.findViewById(R.id.recyclerViewToppers);
        recyclerViewToppers.setHasFixedSize(true);
        recyclerViewToppers.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
        return rootView;
    }

    void init() {
        tag = "frgHome";
        parent = (Home) getActivity();
        session = new AppSession(getContext());

        job.setOnClickListener(this);
        news.setOnClickListener(this);
        downloads.setOnClickListener(this);
        videos.setOnClickListener(this);
        gallery.setOnClickListener(this);
        address.setOnClickListener(this);

        connectYoutube.setOnClickListener(this);
        connectFacebook.setOnClickListener(this);
        connectInsta.setOnClickListener(this);
        connectTelegram.setOnClickListener(this);
        connectTwitter.setOnClickListener(this);
        connectWhatsApp.setOnClickListener(this);
        connectEmail.setOnClickListener(this);
        connectContactUs.setOnClickListener(this);
        connectHelp.setOnClickListener(this);
        box_daily_quiz.setOnClickListener(this);

        if(AppConfig.enableDailyQuiz){
            box_daily_quiz.setVisibility(View.VISIBLE);
        }
        AppMethods.getInstance().productSlider(getContext(), packageSlider,"57",session.getUserId(),packageContainer);

        if(AppConfig.enableBatch){
            AppMethods.getInstance().loadBatch(getContext(), recyclerViewBatch,batchContainer);
        }
        if(AppConfig.enableTodayTopper){
            AppMethods.getInstance().loadToppers(getContext(),recyclerViewToppers,toppersContainerView,10);
        }
        if(AppConfig.enableTodayJoinee){
            AppMethods.getInstance().loadRecentlyJoined(getContext(),recyclerViewRecentlyJoined,session.getUserId(),recentlyContainerView);
        }
        if(AppConfig.enablePublication){
            AppMethods.getInstance().loadBooks(getContext(), recyclerBooks,publicationContainer);
        }
        btnAllToppers.setOnClickListener(v -> startActivity(new Intent(getContext(), TodaysAllToppers.class)));
        loadVideo();
        AppMethods.getInstance().checkDeviceLogin(parent,session.getUserId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_daily_quiz:
                startActivity(new Intent(getContext(), DailyQuiz.class));
                break;
            case R.id.connectYoutube:
                parent.openUrl(getResources().getString(R.string.link_youtube));
                break;
            case R.id.connectFacebook:
                parent.openUrl(getResources().getString(R.string.link_facebook));
                break;
            case R.id.connectInsta:
                parent.openUrl(getResources().getString(R.string.link_insta));
                break;
            case R.id.connectTelegram:
                parent.openUrl(getResources().getString(R.string.link_telegram));
                break;
            case R.id.connectTwitter:
                parent.openUrl(getResources().getString(R.string.link_twitter));
                break;
            case R.id.connectWhatsApp:
                String msg="Hello "+AppConfig.appName+", I am "+session.getName();
                parent.contactWhatsapp(AppConfig.appWhatsAppContact,msg);
                break;
            case R.id.connectEmail:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.contact_email)});
                email.putExtra(Intent.EXTRA_SUBJECT, "");
                email.putExtra(Intent.EXTRA_TEXT, "Dear Team,");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                break;
            case R.id.connectContactUs:
                startActivity(new Intent(getContext(), BranchListing.class));
                break;
            case R.id.connectHelp:
                startActivity(new Intent(getContext(), Help.class));
                break;
            case R.id.job:
                startActivity(new Intent(getContext(), JobListing.class));
                break;
            case R.id.news:
                startActivity(new Intent(getContext(), NewsListing.class));
                break;
            case R.id.downloads:
                startActivity(new Intent(getContext(), Exam.class));
                break;
            case R.id.videos:
                parent.openUrl(getResources().getString(R.string.link_youtube));
                break;
            case R.id.gallery:
                startActivity(new Intent(getContext(), Gallery.class));
                break;
            case R.id.address:
                startActivity(new Intent(getContext(), BranchListing.class));
                break;
        }
    }

    void loadVideo() {
        tag = "loadVideo";
        videoList = new ArrayList<>();
        adapterVideo = new VideoAdapter(getContext(), videoList);
        recyclerViewVideo.setAdapter(adapterVideo);

        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", AppConfig.api_key);
        httpClient.post(AppConfig.moduleApi + "video_list", params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        videoContainer.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                        VideoModel data = null;
                        for (int row = 0; row < jsonArray.length(); row++) {
                            JSONObject value = jsonArray.getJSONObject(row);
                            data = new VideoModel();
                            data.setRowId(value.getInt("video_id"));
                            data.setTitle(value.getString("title"));
                            data.setUrl(value.getString("url"));
                            videoList.add(data);
                        }
                        adapterVideo.notifyDataSetChanged();
                    }else{
                        videoContainer.setVisibility(View.GONE);
                    }
                } catch (JSONException ex) {
                    Log.e(tag, ex.toString());
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(tag, error.toString());
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void onStop() {
        super.onStop();
    }

}
