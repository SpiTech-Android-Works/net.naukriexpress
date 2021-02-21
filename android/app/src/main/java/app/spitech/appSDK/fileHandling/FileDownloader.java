package app.spitech.appSDK.fileHandling;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloader extends Thread implements Runnable{
    private String url;
    private String path;
    private String filename;
    private DownloaderCallback listener=null;

    public FileDownloader(String path, String filename,String url){
        this.path=path;
        this.url=url;
        this.filename=filename;
    }

    public void run(){
        try {
            //--------step1: download file and save it--------------
            File file=new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            URL url = new URL(this.url);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            int total = urlConnection.getContentLength();
            int count;
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(path+"/"+filename);
            byte data[] = new byte[4096];
            long current = 0;
            while ((count = input.read(data)) != -1) {
                current += count;
                if(listener!=null){
                    listener.onProgress((int) ((current*100)/total));
                }
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            if(listener!=null){
                listener.onFinish("File Downloaded & saved by encryption");
            }
        } catch (Exception e) {
            if(listener!=null)
                listener.onError(e.getMessage());
        }
    }
    public void setDownloaderCallback(DownloaderCallback listener){
        this.listener=listener;
    }
    public interface DownloaderCallback{
        void onProgress(int progress);
        void onFinish(String message);
        void onError(String message);
    }
}
