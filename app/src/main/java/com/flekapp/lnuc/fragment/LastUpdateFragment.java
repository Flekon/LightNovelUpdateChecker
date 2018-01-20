package com.flekapp.lnuc.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.adapter.LastUpdateListAdapter;
import com.flekapp.lnuc.data.NovelsRepository;
import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.util.SettingsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LastUpdateFragment extends Fragment {
    private TextView mTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private List<Chapter> mList;
    private List<String> mUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.menu_navigation_last_updates));
        return inflater.inflate(R.layout.fragment_last_update, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTextView = view.findViewById(R.id.text_last_update_date);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout_list_last_update);
        mListView = view.findViewById(R.id.list_last_update);

        mList = new ArrayList<>();
        mUrl = new ArrayList<>();

        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragmentContent();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl.get(position)));
                startActivity(intent);
            }
        });


        refreshFragmentContent();

        LastUpdateListAdapter mAdapter = new LastUpdateListAdapter(getActivity().getApplicationContext(), mList);
        mListView.setAdapter(mAdapter);
    }

    private void refreshFragmentContent() {
        Date lastUpdate = (Date) SettingsManager.getValue(SettingsManager.VALUES_LAST_UPDATE_DATE);
        if (mTextView != null && lastUpdate != null) {
            String date = new SimpleDateFormat(getResources().getString(R.string.date_time_format), Locale.getDefault()).format(lastUpdate);
            mTextView.setText(date);
        }

        mList.clear();
        mUrl.clear();
        List<Chapter> chapters = new ArrayList<>(NovelsRepository.getChaptersFromDB(getActivity().getApplicationContext()).values());
        for (Chapter chapter : chapters) {
            mList.add(chapter);
            mUrl.add(chapter.getUrl());
        }
        mListView.invalidateViews();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}