package com.kdl.naukriexpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.appSDK.Interfaces.CommonInterface;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.R;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    CommonInterface commonInterface;

    // RecyclerView recyclerView;
    public AddressAdapter(Context context1, ArrayList<DataBin> list1, CommonInterface commonInterface) {
        this.context = context1;
        this.list = list1;
        this.commonInterface=commonInterface;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prompt_address_row, parent, false);
        return new AddressAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        String add1=data.getHouseNo()+","+data.getStreet();
        String add2=data.getCity()+"-"+data.getZip()+","+data.getState();

        holder.name.setText(data.getName());
        holder.address1.setText(add1);
        holder.address2.setText(add2);
        holder.itemView.setOnClickListener(v -> {
            String []args={String.valueOf(position)};
            commonInterface.onAdapterItemClick(args);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address1,address2;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address1 = itemView.findViewById(R.id.address1);
            address2 = itemView.findViewById(R.id.address2);
        }
    }

}