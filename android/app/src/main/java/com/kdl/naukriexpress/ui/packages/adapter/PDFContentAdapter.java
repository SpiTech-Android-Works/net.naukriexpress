package com.kdl.naukriexpress.ui.packages.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.common.PDFViewer;
import com.kdl.naukriexpress.ui.packages.ContentPDF;


public class PDFContentAdapter extends RecyclerView.Adapter<PDFContentAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    AppSession session;
    ContentPDF parent;

    public PDFContentAdapter(Context context1, ArrayList<DataBin> list1, ContentPDF parent1) {
        this.context = context1;
        this.list = list1;
        this.session=new AppSession(context1);
        this.parent=parent1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_pdf_row, parent, false);
        return new PDFContentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(data.getTitle());


        File file=new File(context.getFilesDir(),data.getUrl());
        if(file.exists()){
            holder.btnDownload.setText("Open PDF");
        }else{
            holder.btnDownload.setText("Download");
        }
        holder.btnDownload.setOnClickListener(v -> {
            if(file.exists()){
                Intent intent=new Intent(context, PDFViewer.class);
                intent.putExtra("pdfUrl", AppConfig.mediaPDF+data.getUrl());
                intent.putExtra("pdfTitle",data.getTitle());
                intent.putExtra("fileName",data.getUrl());
                intent.putExtra("allow_download",data.getIsDownloadable());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else{
                parent.checkPermision(AppConfig.mediaPDF+data.getUrl(),data.getTitle(),data.getUrl(),holder.btnDownload);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
        public Button btnDownload,btnPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            btnDownload = itemView.findViewById(R.id.btnDownload);

        }
    }

}