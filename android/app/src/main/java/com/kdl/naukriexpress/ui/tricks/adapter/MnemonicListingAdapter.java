package com.kdl.naukriexpress.ui.tricks.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.tricks.MnemonicDetails;


public class MnemonicListingAdapter extends RecyclerView.Adapter<MnemonicListingAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;


    // RecyclerView recyclerView;
    public MnemonicListingAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mnemonic_row, parent, false);
        return new MnemonicListingAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.title.setText(data.getTitle());
        holder.linearLayoutView.setOnClickListener(view -> {
            Intent intent=new Intent(context, MnemonicDetails.class);
            intent.putExtra("mnemonic_id",data.getRowId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public LinearLayout linearLayoutView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            linearLayoutView= itemView.findViewById(R.id.linearLayoutView);
        }
    }


}