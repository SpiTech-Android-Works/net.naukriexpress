package com.kdl.naukriexpress.ui.packages;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppMethods;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.DividerItemDecorator;
import com.kdl.naukriexpress.appSDK.BaseFragment;


public class FrgMock extends BaseFragment {

    RecyclerView recyclerView, recyclerBooks;
    AppSession session;

    public FrgMock() {

    }

    public static FrgMock newInstance(String param1, String param2) {
        FrgMock fragment = new FrgMock();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mock, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerBooks = rootView.findViewById(R.id.recyclerBooks);
        init();
        return rootView;
    }

    void init() {
        tag = "SpitechFrgMock";
        session = new AppSession(getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        recyclerBooks.setHasFixedSize(true);
        recyclerBooks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        //-----divider decoration--------------
        Drawable horizontalDivider = ContextCompat.getDrawable(getContext(), R.drawable.horizontal_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(horizontalDivider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        AppMethods.getInstance().packageList(getActivity(), recyclerView,null, "12");

    }

}
