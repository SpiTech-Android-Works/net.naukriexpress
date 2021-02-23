package com.kdl.naukriexpress.ui.packages.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.CustomUI;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.ui.packages.ProductDetails;


public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    AppSession session;

    public PackageAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        this.session=new AppSession(context1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_row, parent, false);
        return new PackageAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        holder.name.setText(data.getName());
        holder.no_of_test.setText("No. of Test : " + data.getTestCount());
        holder.students.setText("Students(" + data.getStudentCount() + ")");
        holder.rate.setText(CustomUI.getRs(Double.parseDouble(data.getRate())));

        String url = AppConfig.mediaProduct + data.getImage();
        SpiTech.getInstance().loadImage(context,url, holder.image);
        holder.linearLayoutView.setOnClickListener(v -> {
            session.setPackageId(data.getRowId());
            context.startActivity(new Intent(context, ProductDetails.class));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, no_of_test, students, buy_now, rate;
        public CardView linearLayoutView;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            no_of_test = itemView.findViewById(R.id.no_of_test);
            students = itemView.findViewById(R.id.students);
            buy_now = itemView.findViewById(R.id.buy_now);
            rate = itemView.findViewById(R.id.rate);
            image = itemView.findViewById(R.id.image);
            linearLayoutView = itemView.findViewById(R.id.linearLayoutView);
        }
    }

}