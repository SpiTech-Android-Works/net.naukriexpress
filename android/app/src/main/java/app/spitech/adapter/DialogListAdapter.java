package app.spitech.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import app.spitech.R;
import app.spitech.models.DataBin;


public class DialogListAdapter extends RecyclerView.Adapter<DialogListAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    EditText editText;
    AlertDialog dialog;
    TextView hiddenField;

    ArrayList<DataBin> filterArrayList;
    public DialogListAdapter(Activity context1, AlertDialog dialog1, EditText editText1, TextView hiddenField1) {
        this.context = context1;
        this.editText=editText1;
        this.dialog=dialog1;
        this.editText=editText1;
        this.hiddenField=hiddenField1;
        this.list=new ArrayList<>();
        this.filterArrayList=new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_list_item, parent, false);
        return new DialogListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(data.getName());
        holder.itemView.setOnClickListener(v -> {
            editText.setText(data.getName());
            hiddenField.setText(data.getRowId());
            dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }

    //---------------Searching Start----------------
    public void add(DataBin p) {
        list.add(0, p);
        filterArrayList.add(0, p);
        notifyDataSetChanged();
    }

    public void getFilter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.isEmpty()) {
            list.addAll(filterArrayList);
        } else {
            for(DataBin model:filterArrayList) {
                if (model.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    list.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

}