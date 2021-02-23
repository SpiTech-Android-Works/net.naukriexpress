package com.kdl.naukriexpress.ui.test.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.test.TestStart;

public class QuestionGridAdapter extends BaseAdapter {
    Context context;
    TestStart parent;
    private LayoutInflater inflater = null;
    private ArrayList<DataBin> list;

    // RecyclerView recyclerView;
    public QuestionGridAdapter(Context context1, ArrayList<DataBin> listdata) {
        this.context = context1;
        this.list = listdata;
        this.parent = ((TestStart) context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        final QuestionGridAdapter.ViewHolder holder;
        DataBin data = list.get(position);
        if (view == null) {
            vi = inflater.inflate(R.layout.question_number_row, null);
            holder = new QuestionGridAdapter.ViewHolder();
            holder.btnNumber = (Button) vi.findViewById(R.id.btnNumber);
            vi.setTag(holder);
        } else {
            holder = (QuestionGridAdapter.ViewHolder) vi.getTag();
        }

        int number = data.getNumericValue() + 1;
        holder.btnNumber.setText(String.valueOf(number));
        holder.btnNumber.setBackgroundColor(data.getBGColor());
        holder.btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Checking", "Yes");
                parent.loadDetails(data.getNumericValue());
                parent.closeDrawer();
            }
        });
        return vi;
    }

    public static class ViewHolder {
        public Button btnNumber;
    }

    public void setColor(int position, int color) {
        list.get(position).setBGColor(color);
        notifyDataSetChanged();
    }

}
