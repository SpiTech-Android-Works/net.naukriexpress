package com.kdl.naukriexpress.ui.store.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.ConvertTo;
import com.kdl.naukriexpress.appSDK.CustomUI;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.appSDK.Validation;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.packages.ProductDetails;


public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> implements Filterable {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    AppSession session;
    ArrayList<DataBin> filteredArrayList;

    public StoreAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        this.filteredArrayList=list1;
        this.session=new AppSession(context1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_row, parent, false);
        return new StoreAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = filteredArrayList.get(position);

        holder.name.setText(data.getName());
        holder.product_flag.setText(data.getProductFlag());
        if(Validation.isNotEmpty(data.getProductFlag())){
            holder.product_flag.setVisibility(View.VISIBLE);
        }else{
            holder.product_flag.setVisibility(View.GONE);
        }
        holder.short_description.setText(ConvertTo.getSubString(data.getShortDescription(),100));

        Double old_rate=Double.parseDouble(data.getOldRate());
        Double rate=Double.parseDouble(data.getRate());
        Double saving=old_rate-rate;

        holder.old_price.setText(CustomUI.getRs(old_rate));
        holder.old_price.setPaintFlags(holder.old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.price.setText(CustomUI.getRs(rate));
        holder.save.setText(CustomUI.getRs(saving));

        if(old_rate>rate){
            holder.lblSave.setVisibility(View.VISIBLE);
            holder.old_price.setVisibility(View.VISIBLE);
            holder.save.setVisibility(View.VISIBLE);
        }else{
            holder.lblSave.setVisibility(View.GONE);
            holder.old_price.setVisibility(View.GONE);
            holder.save.setVisibility(View.GONE);
        }

        if(Validation.isNotEmpty(data.getImage())){
            String url = AppConfig.mediaProduct + data.getImage();
            SpiTech.getInstance().loadImage(context,url, holder.image);
        }

        if(data.getIsPurchased().equals("0")){
            holder.btnBuy.setText("BuyNow");
        }else{
            holder.btnBuy.setText("Open");
        }
        holder.btnBuy.setOnClickListener(v -> {
            productDetails(data);
        });
        holder.itemView.setOnClickListener(v -> {
            productDetails(data);
        });
    }

    void productDetails(DataBin data){
        int active_tab=0;
        if(data.getIsPurchased().equals("1")){
            active_tab=1;
        }
        Intent intent=new Intent(context, ProductDetails.class);
        intent.putExtra("image",AppConfig.mediaProduct + data.getImage());
        intent.putExtra("package_id",data.getRowId());
        intent.putExtra("package_name",data.getName());
        intent.putExtra("active_tab",active_tab);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return filteredArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, short_description, price,old_price,save,lblSave,product_flag;
        public Button btnBuy;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            product_flag= itemView.findViewById(R.id.product_flag);
            short_description = itemView.findViewById(R.id.short_description);
            price = itemView.findViewById(R.id.price);
            lblSave= itemView.findViewById(R.id.lblSave);
            save = itemView.findViewById(R.id.save);
            old_price = itemView.findViewById(R.id.old_price);
            image = itemView.findViewById(R.id.image);
            btnBuy = itemView.findViewById(R.id.btnBuy);
        }
    }

    //----Search------------
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSearch = constraint.toString();
                if (charSearch.isEmpty()) {
                    filteredArrayList = list;
                } else {
                    ArrayList<DataBin> resultList =new ArrayList<DataBin>();
                    for (DataBin row :list) {
                        if (row.getName().toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row);
                        }
                    }
                    filteredArrayList = resultList;
                }
                FilterResults filterResults =new  FilterResults();
                filterResults.values = filteredArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredArrayList = (ArrayList<DataBin>)results.values;
                notifyDataSetChanged();
            }
        };
    }

}