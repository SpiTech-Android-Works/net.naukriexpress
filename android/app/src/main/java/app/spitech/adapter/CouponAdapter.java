package app.spitech.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.models.DataBin;


public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    AlertDialog dialog;

    // RecyclerView recyclerView;
    public CouponAdapter(Context context1, ArrayList<DataBin> list1,AlertDialog alertDialog) {
        this.context = context1;
        this.list = list1;
        this.dialog=alertDialog;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item, parent, false);
        return new CouponAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        String discount_string = "";
        if (data.getDiscountType().equalsIgnoreCase("Percent")) {
            discount_string = data.getDiscount() + "% Discount";
        } else {
            discount_string = data.getDiscount() + " Flat Discount";
        }
        holder.discount.setText(discount_string);
        holder.title.setText(data.getTitle());
        holder.coupon_code.setText("CODE : " + data.getCouponCode());
        holder.btnApply.setOnClickListener(v -> {
           // ((BookCart) context).promocode.setText(data.getCouponCode());
           // ((BookCart) context).codeApply();
            dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, discount, coupon_code;
        public LinearLayout linearLayoutView;
        public Button btnApply;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            discount = itemView.findViewById(R.id.discount);
            coupon_code = itemView.findViewById(R.id.coupon_code);
            btnApply = itemView.findViewById(R.id.btnApply);
            linearLayoutView = itemView.findViewById(R.id.linearLayoutView);
        }
    }

}