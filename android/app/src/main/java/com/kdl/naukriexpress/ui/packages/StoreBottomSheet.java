package com.kdl.naukriexpress.ui.packages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.Interfaces.CommonInterface;

public class StoreBottomSheet extends BottomSheetDialogFragment {

    private Context mContext;
    private CommonInterface commonInterface;
    Button btnBuy;
    String price;
    public StoreBottomSheet(CommonInterface commonInterface,String price) {
        this.commonInterface = commonInterface;
        this.price=price;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bottom_sheet_store, container, false);
        btnBuy=rootView.findViewById(R.id.btnBuy);
        btnBuy.setText("Buy Now For Rs." + Double.parseDouble(price) + "/-");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    void initView() {
        mContext = getContext();
        btnBuy.setOnClickListener(v -> {
            dismiss();
            commonInterface.buyCourse();
        });
    }

}
