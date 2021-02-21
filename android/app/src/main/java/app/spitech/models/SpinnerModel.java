package app.spitech.models;

public class SpinnerModel {
    public String value;
    public int id;

    public SpinnerModel(int id,String value) {
        this.value = value;
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return value;
    }
}
