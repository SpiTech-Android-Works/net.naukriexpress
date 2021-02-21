package app.spitech.models;

import java.io.Serializable;

/**
 * Created by in.gdc4gpatnew.spitech on 12/22/17.
 */

public class BookModel implements Serializable {

    public String rowId;
    public String name, rate, image;

    public String getRowId() {
        return this.rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRate() {
        return this.rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
