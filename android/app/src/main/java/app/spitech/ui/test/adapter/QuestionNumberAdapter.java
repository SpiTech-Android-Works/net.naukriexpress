package app.spitech.ui.test.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.models.DataBin;
import app.spitech.ui.test.TestStart;


public class QuestionNumberAdapter extends RecyclerView.Adapter<QuestionNumberAdapter.ViewHolder> {
    Context context;
    private ArrayList<DataBin> listdata;

    // RecyclerView recyclerView;
    public QuestionNumberAdapter(Context context1, ArrayList<DataBin> listdata) {
        this.context = context1;
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.question_number_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin myListData = listdata.get(position);
        int number = myListData.getNumericValue() + 1;
        holder.btnNumber.setText(String.valueOf(number));
        holder.btnNumber.setBackgroundColor(myListData.getBGColor());
        holder.btnNumber.setOnClickListener(view -> ((TestStart) context).loadDetails(myListData.getNumericValue()));
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button btnNumber;
        public LinearLayout view;

        public ViewHolder(View itemView) {
            super(itemView);
            this.btnNumber = itemView.findViewById(R.id.btnNumber);
            this.view = itemView.findViewById(R.id.view);
        }
    }

    public void setColor(int position,int color){
        try{
            if(listdata.get(position)!=null){
                listdata.get(position).setBGColor(color);
                notifyDataSetChanged();
            }
        }catch (Exception ex){
            Log.e("setColor Method","IndexOutOfBoundsException");
        }

    }


}