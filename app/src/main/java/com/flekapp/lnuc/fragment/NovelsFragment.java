package com.flekapp.lnuc.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.data.entity.Source;
import com.flekapp.lnuc.service.Refresher;

import java.util.ArrayList;
import java.util.List;

public class NovelsFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private List<String> mList;
    private List<String> mUrl;

    public NovelsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.menu_navigation_novels));
        return inflater.inflate(R.layout.fragment_novels, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout_list_novels);
        mListView = view.findViewById(R.id.list_novels);

        mList = new ArrayList<>();
        mUrl = new ArrayList<>();
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragmentContent();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl.get(position)));
                startActivity(intent);
            }
        });


        refreshFragmentContent();

        ListAdapter mAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, mList);
        mListView.setAdapter(mAdapter);
    }

    private void refreshFragmentContent() {
        mList.clear();
        mUrl.clear();
        mListView.invalidateViews();
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                mListView.invalidateViews();
                mSwipeRefreshLayout.setRefreshing(false);

                return false;
            }
        });
        new Refresher(getActivity().getApplicationContext()).refreshNovels(Source.LNMTL, new Refresher.onNovelsRefreshed() {
            @Override
            public void onRefresh(List<Novel> novels) {
                for (Novel novel : novels) {
                    mList.add(String.format("(%s) %s",
                            novel.getShortName(), novel.getName()));
                    mUrl.add(novel.getUrl());
                }
                handler.sendEmptyMessage(0);
            }
        });
    }
}