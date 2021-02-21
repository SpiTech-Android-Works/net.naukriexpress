package app.spitech.ui.branch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.appSDK.AppSession;
import app.spitech.models.DataBin;


public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    AppSession session;

    public BranchAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        this.session=new AppSession(context1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.branch_row, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public WebView address;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            address = itemView.findViewById(R.id.address);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.title.setText(data.getTitle());
        holder.address.loadData(data.getDescription(),"text/html","UTF-8");
    }

}