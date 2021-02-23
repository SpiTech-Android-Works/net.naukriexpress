package com.kdl.naukriexpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import com.kdl.naukriexpress.models.SpinnerStateModel;

public class SpinnerStateAdapter extends ArrayAdapter<SpinnerStateModel> {

    private ArrayList<SpinnerStateModel> myarrayList;

    public SpinnerStateAdapter(Context context, int textViewResourceId, ArrayList<SpinnerStateModel> modelArrayList) {
        super(context, textViewResourceId, modelArrayList);
        this.myarrayList = modelArrayList;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Nullable
    @Override
    public SpinnerStateModel getItem(int position) {
        return myarrayList.get(position);
    }

    @Override
    public int getCount() {
        int count = myarrayList.size();
        return count;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    private View getCustomView(int position, ViewGroup parent) {
        SpinnerStateModel model = getItem(position);
        View spinnerRow = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView label = spinnerRow.findViewById(android.R.id.text1);
        label.setText(String.format("%s", model != null ? model.getState() : ""));
        return spinnerRow;
    }
}
