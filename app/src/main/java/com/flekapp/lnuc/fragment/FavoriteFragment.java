package com.flekapp.lnuc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.adapter.FavoritesItemTouchHelper;
import com.flekapp.lnuc.adapter.RecyclerAdapterFavorites;
import com.flekapp.lnuc.data.NovelsRepository;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.service.Refresher;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mEmptyView;

    private Context mContext;
    private List<Novel> mNovels;
    private RecyclerAdapterFavorites mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.menu_navigation_favorites));
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        mContext = getActivity().getApplicationContext();

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout_list_favorites);
        mRecyclerView = view.findViewById(R.id.recycler_view_favorites);
        mEmptyView = view.findViewById(R.id.list_favorites_empty_text);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragmentContent();
            }
        });

        FavoritesItemTouchHelper itemTouchHelperCallback =
                new FavoritesItemTouchHelper(new FavoritesItemTouchHelper.SwipeListener() {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, FavoritesItemTouchHelper.SwipeDirection direction, int position) {
                switch (direction) {
                    case LEFT:
                        NovelsRepository.deleteFavorite(mContext,
                                mNovels.get(position));
                        mNovels.remove(mNovels.get(position));
                        mAdapter.notifyItemRemoved(position);
                        break;
                    case RIGHT:
                        new Refresher(mContext).startRefreshFavorite(mNovels.get(position));
                        mAdapter.notifyItemChanged(position);
                        break;
                }
            }
        });
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        mNovels = new ArrayList<>();
        mAdapter = new RecyclerAdapterFavorites(mContext, mNovels);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSwipeRefreshLayout.setRefreshing(true);
        refreshFragmentContent();
    }

    private void refreshFragmentContent() {
        List<Novel> favorites = new ArrayList<>(NovelsRepository
                .getFavoritesFromDB(mContext).values());
        mNovels.clear();
        mNovels.addAll(favorites);

        mAdapter.notifyDataSetChanged();
        checkIsViewEmpty();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void checkIsViewEmpty() {
        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }
}
