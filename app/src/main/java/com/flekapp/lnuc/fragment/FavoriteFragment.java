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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

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
                new FavoritesItemTouchHelper(new FavoritesItemTouchHelper.OnSwipeListener() {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, FavoritesItemTouchHelper.SwipeDirection direction, int position) {
                switch (direction) {
                    case LEFT:
                        showRemoveNovelDialog(position);
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
        mAdapter.setOnMoreButtonClickListener(new RecyclerAdapterFavorites.OnMoreButtonClickListener() {
            @Override
            public void onClick(View view, int position) {
                showFavoritesPopupMenu(view, position);
            }
        });

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

    private void showFavoritesPopupMenu(View view, final int position) {
        final Novel novel = mNovels.get(position);
        PopupMenu menu = new PopupMenu(mContext, view);
        menu.inflate(R.menu.menu_popup_favorites);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_popup_favorites_refresh:
                        new Refresher(mContext).startRefreshFavorite(novel);
                        return true;
                    case R.id.menu_popup_favorites_recache:
                        Toast.makeText(mContext, "Recache", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_popup_favorites_open_url:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(novel.getUrl()));
                        startActivity(intent);
                        return true;
                    case R.id.menu_popup_favorites_remove:
                        showRemoveNovelDialog(position);
                        return true;
                    default:
                        return false;
                }
            }
        });
        menu.show();
    }

    private void showRemoveNovelDialog(final int position) {
        final Novel novel = mNovels.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format("Remove from favorites ?\n[ %s ]", novel.getName()));
        builder.setCancelable(true);
        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAdapter.notifyItemChanged(position);
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean isRemoved = NovelsRepository.deleteFavorite(mContext, novel);
                        if (isRemoved) {
                            NovelsRepository.deleteFavorite(mContext, mNovels.get(position));
                            mNovels.remove(mNovels.get(position));
                            mAdapter.notifyItemRemoved(position);
                        }
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
