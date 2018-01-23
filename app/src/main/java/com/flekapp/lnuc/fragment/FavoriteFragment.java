package com.flekapp.lnuc.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.Toast;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.adapter.ListAdapterFavorites;
import com.flekapp.lnuc.data.NovelsRepository;
import com.flekapp.lnuc.data.entity.Novel;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;

    private ListAdapterFavorites mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.menu_navigation_favorites));
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout_list_favorites);
        mListView = view.findViewById(R.id.list_favorites);
        mListView.setEmptyView(view.findViewById(R.id.list_favorites_empty_text));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragmentContent();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAdapter.getFavorite(position).getUrl()));
                startActivity(intent);
            }
        });
        final Context context = getActivity().getApplicationContext();
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                final Novel novel = mAdapter.getFavorite(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(String.format("Remove \"%s\" from favorites ?", novel.getName()));
                builder.setCancelable(true);

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean isRemoved = NovelsRepository.deleteFavorite(context, novel);
                                if (isRemoved) {
                                    refreshFragmentContent();
                                    Toast.makeText(context, "Novel removed from favorites !", Toast.LENGTH_LONG).show();
                                }
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }
        });

        mAdapter = new ListAdapterFavorites(getActivity().getApplicationContext(), new ArrayList<Novel>());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSwipeRefreshLayout.setRefreshing(true);
        refreshFragmentContent();
    }

    private void refreshFragmentContent() {
        List<Novel> favorites = new ArrayList<>(NovelsRepository
                .getFavoritesFromDB(getActivity().getApplicationContext()).values());
        mAdapter.setNovels(favorites);
        mListView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}