package app.spitech.ui.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;

public class PDFViewer extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private TextView pdfTitle;
    private PDFView pdfView;
    String fileName = "";
    private ImageView btnShare;
    Integer pageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_viewer);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(PDFViewer.this, "PDFViewer", "PDF Viewer");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("PDF Viewer");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        //---------Basic End------------
        btnShare= findViewById(R.id.btnShare);
        pdfTitle = findViewById(R.id.pdfTitle);
        pdfView = findViewById(R.id.pdfView);
        if (getIntent().hasExtra("fileName")) {
            pdfTitle.setText(getIntent().getExtras().getString("pdfTitle"));
            fileName = getIntent().getExtras().getString("fileName");
            String allow_download= getIntent().getExtras().getString("allow_download");
            if(allow_download.equalsIgnoreCase("1")){
                btnShare.setVisibility(View.VISIBLE);
            }
            showPDF();
        }
        btnShare.setOnClickListener(v -> shareFile());
    }

    void showPDF() {
        try{
            File file = new File(getFilesDir(),fileName);
            if (file.exists()) {
                //Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
                pdfView.fromFile(file)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableAntialiasing(true)
                        .onLoad(this)
                        .spacing(10)
                        .load();
            } else {
                showLog("PDF", "PDF file not downloaded into SDCARD");
            }
        }catch (Exception ex){
            Log.e("showPDF",ex.toString());
        }
    }

    void shareFile(){
        try{
            File file = new File(getFilesDir(),fileName);
            String provider= AppConfig.fileProvider;
            Uri uri = FileProvider.getUriForFile(context, provider, file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
            startActivity(Intent.createChooser(shareIntent, "Share it"));
        }catch (Exception ex){
            Log.e("shareFile-Exception",ex.toString());
            showToast("File is not sharable");
        }
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", fileName, page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }
    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {
            Log.e(tag, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
}