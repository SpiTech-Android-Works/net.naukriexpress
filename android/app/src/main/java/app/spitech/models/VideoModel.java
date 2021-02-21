package app.spitech.models;

import java.io.Serializable;

public class VideoModel implements Serializable {

    public int rowId;
    public String title, publishDate, url;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRowId() {
        return this.rowId;
    }

    public void setRowId(int newsId) {
        this.rowId = rowId;
    }

    public String getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

}