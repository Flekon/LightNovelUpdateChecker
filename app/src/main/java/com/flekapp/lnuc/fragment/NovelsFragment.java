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

    private ListAdapter mAdapter;
    private List<String> mList;
    private List<Novel> mNovels;

    public NovelsFragment() {
        mList = new ArrayList<>();
        mNovels = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.menu_navigation_novels));
        View view = inflater.inflate(R.layout.fragment_novels, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout_list_novels);
        mListView = view.findViewById(R.id.list_novels);
        mListView.setEmptyView(view.findViewById(R.id.list_novels_empty_text));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragmentContent();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mNovels.get(position).getUrl()));
                startActivity(intent);
            }
        });
        /*mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
                builder.setMessage("Add novel to favorite ?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                NovelsDBHelper mDbHelper = new NovelsDBHelper(getActivity().getApplicationContext());
                                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                                NovelsRepository.addFavorite(db, mNovels.get(position));
                                dialog.cancel();
                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });*/

        mAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, mList);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mListView.setAdapter(mAdapter);
    }

    private void refreshFragmentContent() {
        mList.clear();
        mNovels.clear();
        mListView.setAdapter(mAdapter);
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                mListView.setAdapter(mAdapter);
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
                    mNovels.add(novel);
                }
                handler.sendEmptyMessage(0);
            }
        });
    }
}