package app.spitech.ui.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.ComingSoon;
import app.spitech.appSDK.Validation;
import app.spitech.models.DataBin;
import app.spitech.models.QuestionData;
import app.spitech.ui.test.adapter.QuestionGridAdapter;
import app.spitech.ui.test.adapter.QuestionNumberAdapter;

public class TestStart extends BaseActivity implements View.OnClickListener {

    public final long interval = 1 * 1000;
    public ArrayList<QuestionData> dataList;
    public List<String> aAnswer;
    public ArrayList<DataBin> aQuestionNumbers;
    public List<String> aReporting;
    public QuestionNumberAdapter adapter;
    public QuestionGridAdapter adapter1;
    public RecyclerView questionListing;
    public GridView questionsGrid;
    public int totalQuestions = 0;
    public int question_id, current_question = 0;
    public WebView question, instruction, option_a, option_b, option_c, option_d,option_e;
    public RadioButton rdb_option_a, rdb_option_b, rdb_option_c, rdb_option_d, rdb_option_e;
    public DrawerLayout drawer;
    public ImageView questionPad;
    public TextView lnkReport;
    public LinearLayout boxSubmit;
    public TextView timer;
    public CountDownTimer countDownTimer;
    public AlertDialog alertDialog;
    public TextView btnBack, btnClear, btnSave;
    public int counter = 3;
    private TableRow option_e_layout;
    ///---------timer---------
    long startTime = 60 * 1000;
    String test_id, result_id;
    String correctOption = "";
    TextView questionNo, positiveMarks, negativeMarks;
    String reporting_msg, answer;
    String qry = "Do you want to Submit Test?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.test_start);
        init();
    }

    void init() {
        load(TestStart.this, "SpiTechTestStart", "TestStart");
        countdownTimer();

        timer = findViewById(R.id.timer);
        timer.setText("00:00:00");
        if (getIntent().hasExtra("duration") && getIntent().hasExtra("test_id") && getIntent().hasExtra("result_id")) {
            test_id = getIntent().getExtras().getString("test_id");
            result_id = getIntent().getExtras().getString("result_id");
            startTime = getIntent().getExtras().getInt("duration");
            startTime = startTime * 1000 * 60;
        } else {
            Log.e(tag, "Test duration is not defined properly");
        }
        countDownTimer = new MyCountDownTimer(startTime, interval);
        countDownTimer.start();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        questionPad = findViewById(R.id.questionPad);
        question =  findViewById(R.id.question);
        instruction =  findViewById(R.id.instruction);
        option_a =  findViewById(R.id.option_a);
        option_b =  findViewById(R.id.option_b);
        option_c =  findViewById(R.id.option_c);
        option_d =  findViewById(R.id.option_d);
        option_e =  findViewById(R.id.option_e);
        option_e_layout=  findViewById(R.id.option_e_layout);

        rdb_option_a = findViewById(R.id.rdb_option_a);
        rdb_option_b = findViewById(R.id.rdb_option_b);
        rdb_option_c = findViewById(R.id.rdb_option_c);
        rdb_option_d = findViewById(R.id.rdb_option_d);
        rdb_option_e = findViewById(R.id.rdb_option_e);

        lnkReport = findViewById(R.id.lnkReport);
        boxSubmit =  findViewById(R.id.boxSubmit);
        questionNo = findViewById(R.id.questionNo);
        positiveMarks = findViewById(R.id.positiveMarks);
        negativeMarks = findViewById(R.id.negativeMarks);

        btnClear = findViewById(R.id.btnClear);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);

        questionListing = findViewById(R.id.questionListing);
        questionListing.setHasFixedSize(true);
        questionListing.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        questionsGrid =  findViewById(R.id.questionsGrid);

        questionPad.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        boxSubmit.setOnClickListener(this);
        lnkReport.setOnClickListener(this);

        option_a.setOnTouchListener((v, event) -> {
            checkAns(rdb_option_a);
            return false;
        });
        option_b.setOnTouchListener((v, event) -> {
            checkAns(rdb_option_b);
            return false;
        });
        option_c.setOnTouchListener((v, event) -> {
            checkAns(rdb_option_c);
            return false;
        });
        option_d.setOnTouchListener((v, event) -> {
            checkAns(rdb_option_d);
            return false;
        });
        option_e.setOnTouchListener((v, event) -> {
            checkAns(rdb_option_e);
            return false;
        });

        rdb_option_a.setOnClickListener(view -> checkAns(rdb_option_a));
        rdb_option_b.setOnClickListener(view -> checkAns(rdb_option_b));
        rdb_option_c.setOnClickListener(view -> checkAns(rdb_option_c));
        rdb_option_d.setOnClickListener(view -> checkAns(rdb_option_d));
        rdb_option_e.setOnClickListener(view -> checkAns(rdb_option_e));
        loadList();
    }

    void save(String action) {
        try {
            reporting_msg = aReporting.get(current_question);
            String strAnswer = dataList.get(current_question).getQueId() + "," + answer +"," + reporting_msg + "," + dataList.get(current_question).getCorrectOption();
            strAnswer += "," + dataList.get(current_question).getPositiveMarks() + "," + dataList.get(current_question).getNegativeMarks() + "," + dataList.get(current_question).getMasterQueId();
            strAnswer += "," +action;
            aAnswer.set(current_question, strAnswer);
            Log.e(tag, "----------------------Save---------------");
            Log.e(tag, "Inserted :" + answer);
            Log.e(tag, "Current Position :" + current_question);
            Log.e(tag, "aAnswer Size :" + aAnswer.size());
            Log.e(tag, "Data in aAnswer on Position :" + current_question + " is :" + aAnswer.get(current_question));
            Log.e(tag, "Question in dataList on Position :" + current_question + " is :" + dataList.get(current_question).getQuestion());
        } catch (Exception ex) {
            Log.e(tag, "Error in save method" + ex.getMessage());
        }
    }

    void next(){
        try{
            if (current_question == dataList.size() - 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setCancelable(false);
                builder.setTitle("Confirmation?");
                builder.setMessage("This is the last question for this test. Do you want to submit the test?");
                builder.setNegativeButton("Yes", (dialog, which) -> finishTest());
                builder.setPositiveButton("No", (dialog, which) -> dialog.dismiss());
                alertDialog = builder.create();
                alertDialog.show();
            } else {
                current_question = current_question + 1;
                loadDetails(current_question);
            }
        }catch (Exception ex){
            Log.e("NextButtonClick",ex.getMessage());
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnClear) {
            clearChoice();
        } else if (id == R.id.boxSubmit) {
            finishTestConfirmation();
        } else if (id == R.id.lnkReport) {
            openReportBox();
        } else if (id == R.id.btnSave) {
            next();
        } else if (id == R.id.btnBack) {
            if (current_question > 0) {
                current_question = current_question - 1;
            } else if (current_question == 0) {
                Toast.makeText(TestStart.this, "This is the first question.", Toast.LENGTH_LONG).show();
            }
            loadDetails(current_question);
        } else if (id == R.id.questionPad) {
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.closeDrawer(Gravity.RIGHT);
            } else {
                drawer.openDrawer(Gravity.RIGHT);
            }
        }
    }


    //---------------------TEST BUSINESS LOGIC------------
    public void loadList() {
        try {
            aAnswer = new ArrayList<>();
            aReporting = new ArrayList<>();
            dataList = new ArrayList<>();

            aQuestionNumbers = new ArrayList<>();

            //---Top Question Slider Load----------------
            adapter = new QuestionNumberAdapter(context, aQuestionNumbers);
            questionListing.setAdapter(adapter);

            //---Side Grid Question Slider Load----------------
            adapter1 = new QuestionGridAdapter(context, aQuestionNumbers);
            questionsGrid.setAdapter(adapter1);

            showProgress(context, "Loading...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.testApi + "test_question",
                    response -> {
                        closeProgress();
                        /*Log.e(tag, response);*/
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                                QuestionData data = null;
                                DataBin obj = null;
                                totalQuestions = jsonArray.length();
                                for (int row = 0; row < jsonArray.length(); row++) {
                                    JSONObject value = jsonArray.getJSONObject(row);

                                    data = new QuestionData();
                                    obj = new DataBin();
                                    obj.setNumericValue(row);
                                    obj.setBGColor(getResources().getColor(R.color.gray));
                                    aQuestionNumbers.add(obj);

                                    aAnswer.add(row, "");
                                    aReporting.add(row, "");
                                    data.setQuesNo(row + 1);
                                    data.setQueId(value.getInt("question_id"));
                                    data.setMasterQueId(value.getInt("master_question_id"));
                                    data.setQuestion(value.getString("question"));
                                    data.setOptionA(value.getString("option_a"));
                                    data.setOptionB(value.getString("option_b"));
                                    data.setOptionC(value.getString("option_c"));
                                    data.setOptionD(value.getString("option_d"));
                                    data.setOptionE(value.getString("option_e"));
                                    data.setCorrectOption(value.getString("correct_option"));
                                    data.setPositiveMarks(value.getString("positive_marks"));
                                    data.setNegativeMarks(value.getString("negative_marks"));
                                    dataList.add(data);
                                }
                                adapter.notifyDataSetChanged();
                                adapter1.notifyDataSetChanged();
                                loadDetails(current_question);
                            }
                        } catch (JSONException ex) {
                            Log.e(tag, ex.toString());
                        }
                    },
                    error -> {
                        closeProgress();
                        Log.e(tag, error.toString());
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("api_key", AppConfig.api_key);
                    params.put("test_id", test_id);
                    params.put("lang", "hindi");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        } catch (Exception ex) {
            Log.e(tag, "Error:" + ex.getMessage());
        }
        setAllColor();
    }

    public void loadDetails(int current_question) {
        try {
            this.current_question=current_question;
            loadBlankRadioButtons();
            questionListing.getLayoutManager().scrollToPosition(current_question);
            setColor(current_question);

            Log.e(tag, "----------------------LoadDetails---------------");
            Log.e(tag, "Current Question :" + current_question);
            Log.e(tag, "Data in aAnswer on Current Position :" + current_question + " is :" + aAnswer.get(current_question));

            Log.e(tag, "Question in dataList on Current Position :" + current_question + " is :" + dataList.get(current_question).getQuestion());
            // str_question = dataList.get(current_question).getShortQuestion();
            String question_no = dataList.get(current_question).getQuesNo() + "/" + totalQuestions;
            questionNo.setText(question_no);

            question.loadDataWithBaseURL("", dataList.get(current_question).getQuestion(), "text/html", "UTF-8", "");

            positiveMarks.setText(" +Ve " + dataList.get(current_question).getPositiveMarks());
            negativeMarks.setText(" -Ve " + dataList.get(current_question).getNegativeMarks());

            question_id = dataList.get(current_question).getQueId();
            option_a.loadDataWithBaseURL("", dataList.get(current_question).getOptionA(), "text/html", "UTF-8", "");
            option_b.loadDataWithBaseURL("", dataList.get(current_question).getOptionB(), "text/html", "UTF-8", "");
            option_c.loadDataWithBaseURL("", dataList.get(current_question).getOptionC(), "text/html", "UTF-8", "");
            option_d.loadDataWithBaseURL("", dataList.get(current_question).getOptionD(), "text/html", "UTF-8", "");
            option_e.loadDataWithBaseURL("", dataList.get(current_question).getOptionE(), "text/html", "UTF-8", "");
            if(Validation.isNotEmpty(dataList.get(current_question).getOptionE())){
                option_e_layout.setVisibility(View.VISIBLE);
            }else{
                option_e_layout.setVisibility(View.GONE);
            }
            correctOption = dataList.get(current_question).getCorrectOption();

            switch (getTempAns(current_question)) {
                case "a":
                    //rdb_option_a.setChecked(true);
                    checkAns(rdb_option_a);
                    break;
                case "b":
                    checkAns(rdb_option_b);
                    break;
                case "c":
                    checkAns(rdb_option_c);
                    break;
                case "d":
                    checkAns(rdb_option_d);
                    break;
                case "e":
                    checkAns(rdb_option_e);
                    break;
                default:
                    break;
            }

        } catch (Exception ex) {
            Log.e(tag, ex.toString());
        }
    }

    void clearChoice() {
        try{
            aAnswer.set(current_question, "");
            setColor(current_question);
            loadBlankRadioButtons();
        }catch (Exception ex){
            Log.e(tag,ex.getMessage());
        }
    }

    void loadBlankRadioButtons() {
        rdb_option_a.setChecked(false);
        rdb_option_b.setChecked(false);
        rdb_option_c.setChecked(false);
        rdb_option_d.setChecked(false);
        rdb_option_e.setChecked(false);

        rdb_option_a.setTextColor(Color.BLACK);
        rdb_option_b.setTextColor(Color.BLACK);
        rdb_option_c.setTextColor(Color.BLACK);
        rdb_option_d.setTextColor(Color.BLACK);
        rdb_option_e.setTextColor(Color.BLACK);
    }

    String getTempAns(int current_question) {
        String str = aAnswer.get(current_question);
        //Log.e(tag,"Yes"+str);
        List<String> items = Arrays.asList(str.split("\\s*,\\s*"));
        Log.e(tag, "Size" + items.size());
        String temp_answer = "";
        if (items.size() > 1) {
            temp_answer = items.get(1);
        }
        Log.e(tag, "TempAns :" + temp_answer);
        return temp_answer;
    }

    //------------------------------COLOR SETTING START----------------
    void setAllColor(){
        for(int pos=0;pos<adapter.getItemCount();pos++){
            adapter.setColor(pos,getResources().getColor(R.color.gray));
        }
    }
    void setColor(int current_question){
        try{
            for(int pos=0;pos<adapter.getItemCount();pos++){
                String str = aAnswer.get(pos);
                List<String> items = Arrays.asList(str.split("\\s*,\\s*"));
                Log.e("setColor Size",""+items.size());
                if(items.size()==8){
                    if(items.get(7).equalsIgnoreCase("save")){
                        setButtonColor(pos,getResources().getColor(R.color.attempted_question));
                    }else  if(items.get(7).equalsIgnoreCase("mark")){
                        setButtonColor(pos,getResources().getColor(R.color.marked_question));
                    }
                }else{
                    setButtonColor(pos,getResources().getColor(R.color.skipped_question));
                }
            }
            setButtonColor(current_question,getResources().getColor(R.color.current_question));
        }catch (Exception ex){
            setButtonColor(current_question,getResources().getColor(R.color.skipped_question));
            Log.e("setColor",ex.getMessage());
        }
    }
    void setButtonColor(int pos,int color){
        adapter.setColor(pos,color);
        adapter1.setColor(pos,color);
    }
    //------------------------------COLOR SETTING STOP----------------

    public void checkAns(View v) {
        answer = "";
        int choice = v.getId();
        switch (choice) {
            case R.id.rdb_option_a:
                answer = "a";
                rdb_option_a.setChecked(true);
                rdb_option_b.setChecked(false);
                rdb_option_c.setChecked(false);
                rdb_option_d.setChecked(false);
                rdb_option_e.setChecked(false);
                break;
            case R.id.rdb_option_b:
                answer = "b";
                rdb_option_a.setChecked(false);
                rdb_option_b.setChecked(true);
                rdb_option_c.setChecked(false);
                rdb_option_d.setChecked(false);
                rdb_option_e.setChecked(false);
                break;
            case R.id.rdb_option_c:
                answer = "c";
                rdb_option_a.setChecked(false);
                rdb_option_b.setChecked(false);
                rdb_option_c.setChecked(true);
                rdb_option_d.setChecked(false);
                rdb_option_e.setChecked(false);
                break;
            case R.id.rdb_option_d:
                answer = "d";
                rdb_option_a.setChecked(false);
                rdb_option_b.setChecked(false);
                rdb_option_c.setChecked(false);
                rdb_option_d.setChecked(true);
                rdb_option_e.setChecked(false);
                break;
            case R.id.rdb_option_e:
                answer = "e";
                rdb_option_a.setChecked(false);
                rdb_option_b.setChecked(false);
                rdb_option_c.setChecked(false);
                rdb_option_d.setChecked(false);
                rdb_option_e.setChecked(true);
                break;
        }
        save("save");
        setColor(current_question);
        Log.e(tag, "SELECTED : " + answer);
    }

    void finishTestConfirmation() {
        confirm(context, "Confirmation", qry);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void countdownTimer() {
        try {
            //----------------begin adding question buttons-----------------
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.countdown_timer, null);
            final TextView timer = promptsView.findViewById(R.id.timer);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);
            alertDialogBuilder.setCancelable(false);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            new CountDownTimer(4000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timer.setText(String.valueOf(counter));
                    counter--;
                }

                @Override
                public void onFinish() {
                    alertDialog.dismiss();
                }
            }.start();
            alertDialog.show();
        }catch (Exception ex){
            Log.e("countdownTimer",ex.getMessage());
        }
    }

    void confirm(Context context, String str_title, String str_message) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_confirm, null);
        TextView title = promptsView.findViewById(R.id.title);
        TextView message = promptsView.findViewById(R.id.message);
        Button btnYes = (Button) promptsView.findViewById(R.id.btnYes);
        Button btnNo = (Button) promptsView.findViewById(R.id.btnNo);

        title.setText(str_title);
        message.setText(str_message);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        btnYes.setOnClickListener(view -> {
            finishTest();
            alertDialog.dismiss();
        });
        btnNo.setOnClickListener(view -> alertDialog.dismiss());

        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }

    void finishTest() {
        showProgress(context, "Loading...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.testApi + "finish_test",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            if (test_id.equalsIgnoreCase("889")) {
                                startActivity(new Intent(context, ComingSoon.class));
                            } else {
                                session.setResultId(result_id);
                                Intent intent = new Intent(context, ScoreCard.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                    closeProgress();
                },
                error -> {
                    Log.e(tag, error.toString());
                    closeProgress();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // String repostingList = new Gson().toJson(aReporting);
                String answerList = new Gson().toJson(aAnswer);
                Map<String, String> params = new HashMap();

                params.put("api_key", AppConfig.api_key);
                params.put("customer_id", session.getUserId());
                params.put("result_id", result_id);
                params.put("aAnswer", answerList);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //-------timer start-------------
    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            confirm(context, "Time finished", "Test time is finished, Click on Yes Button to see the result");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long totalSecs = millisUntilFinished / 1000;
            long hours = totalSecs / 3600;
            long minutes = (totalSecs % 3600) / 60;
            long seconds = totalSecs % 60;
            String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            timer.setText(timeString);
        }
    }

    //timer stop---------------
    public void closeDrawer() {
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawers();
        }
    }

    @Override
    public void onBackPressed() {
        closeDrawer();
    }

    void openReportBox() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.prompt_question_report, null);
        EditText txtOther = (EditText) view.findViewById(R.id.txtOther);
        CheckBox ckb_spelling = (CheckBox) view.findViewById(R.id.ckb_spelling);
        CheckBox ckb_image = (CheckBox) view.findViewById(R.id.ckb_image);
        CheckBox ckb_incomplete = (CheckBox) view.findViewById(R.id.ckb_incomplete);
        CheckBox ckb_direction = (CheckBox) view.findViewById(R.id.ckb_direction);
        CheckBox ckb_other = (CheckBox) view.findViewById(R.id.ckb_other);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        ckb_other.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (ckb_other.isChecked()) {
                txtOther.setText("");
                txtOther.setVisibility(View.VISIBLE);
            } else {
                txtOther.setVisibility(View.GONE);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.create();
        final AlertDialog dialog = builder.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSubmit.setOnClickListener(v -> {
            showToast("Question Reporting Saved Successfully.");
            dialog.dismiss();
        });
    }
}
