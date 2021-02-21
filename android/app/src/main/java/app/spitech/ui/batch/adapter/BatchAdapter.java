package app.spitech.ui.batch.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.appSDK.Validation;
import app.spitech.models.DataBin;
import app.spitech.ui.batch.BatchDetails;


public class BatchAdapter extends RecyclerView.Adapter<BatchAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;

    public BatchAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public BatchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_row, parent, false);
        return new BatchAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BatchAdapter.ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(data.getName());
        holder.timing.setText(data.getTiming());
        if(Validation.isNotEmpty(data.getIsPurchased())){
            holder.btnJoin.setText("Attend");
            holder.btnJoin.setBackgroundResource(R.drawable.rounded_green);
        }
        holder.itemView.setOnClickListener(v -> {
            onItemClick(data);
        });
        holder.btnJoin.setOnClickListener(v -> {
            onItemClick(data);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button btnJoin;
        public TextView name, timing;
        public ViewHolder(View itemView) {
            super(itemView);
            btnJoin = itemView.findViewById(R.id.btnJoin);
            name = itemView.findViewById(R.id.name);
            timing = itemView.findViewById(R.id.timing);
        }
    }

    void onItemClick(DataBin data){
        Intent intent = new Intent(context, BatchDetails.class);
        intent.putExtra("batch_id", data.getRowId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}