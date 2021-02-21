package app.spitech.ui.orders.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.appSDK.ConvertTo;
import app.spitech.appSDK.HelperMethods;
import app.spitech.models.DataBin;
import app.spitech.ui.orders.OrderDetails;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;

    public OrderAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row, parent, false);
        return new OrderAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.order_id.setText("Order No: "+data.getRowId());
        holder.order_date_time.setText("Date: "+ConvertTo.customDate(data.getDate(),"yyyy-MM-dd HH:mm:ss","dd/MMM/yyyy hh:mm:ss a"));
        holder.net_total.setText(HelperMethods.getRs(Double.parseDouble(data.getRate())));
        holder.btnDetails.setOnClickListener(v -> {
            orderDetails(data);
        });
        holder.itemView.setOnClickListener(v -> {
            orderDetails(data);
        });
        holder.itemContainer.setOnClickListener(v -> {
            orderDetails(data);
        });

    }

    void orderDetails(DataBin data){
        Intent intent=new Intent(context, OrderDetails.class);
        intent.putExtra("order_id",data.getRowId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView order_id, order_date_time,net_total;
        public ImageView btnDetails;
        public LinearLayout itemContainer;
        public ViewHolder(View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.order_id);
            order_date_time = itemView.findViewById(R.id.order_date_time);
            net_total = itemView.findViewById(R.id.net_total);
            btnDetails= itemView.findViewById(R.id.btnDetails);
            itemContainer= itemView.findViewById(R.id.itemContainer);
        }
    }
}