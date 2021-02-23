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


public class FrgTopicWise extends BaseFragment {


    private RecyclerView recyclerView, recyclerBooks;

    public FrgTopicWise() {

    }

    public static FrgTopicWise newInstance(String param1, String param2) {
        FrgTopicWise fragment = new FrgTopicWise();
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
        View rootView = inflater.inflate(R.layout.fragment_topic_wise, container, false);
        session=new AppSession(getContext());
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerBooks = rootView.findViewById(R.id.recyclerBooks);
        init();
        return rootView;
    }

    void init() {
        tag = "SpitechFrgTopicWise";
        session = new AppSession(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerBooks.setHasFixedSize(true);
        recyclerBooks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        //-----divider decoration--------------
        Drawable horizontalDivider = ContextCompat.getDrawable(getContext(), R.drawable.horizontal_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(horizontalDivider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        AppMethods.getInstance().packageList(getActivity(), recyclerView,null, "11");

    }

}
