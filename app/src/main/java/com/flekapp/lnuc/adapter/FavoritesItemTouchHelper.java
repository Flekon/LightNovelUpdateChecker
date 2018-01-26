package com.flekapp.lnuc.adapter;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;

public class FavoritesItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private SwipeListener swipeListener;

    public FavoritesItemTouchHelper(@NonNull SwipeListener listener) {
        super(0, LEFT | RIGHT);
        swipeListener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        switch (direction) {
            case RIGHT:
                swipeListener.onSwiped(viewHolder, SwipeDirection.RIGHT, viewHolder.getAdapterPosition());
                break;
            case LEFT:
                swipeListener.onSwiped(viewHolder, SwipeDirection.LEFT, viewHolder.getAdapterPosition());
                break;
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((RecyclerAdapterFavorites.MyViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View viewForeground = ((RecyclerAdapterFavorites.MyViewHolder) viewHolder).viewForeground;
        final View viewRightSwipe = ((RecyclerAdapterFavorites.MyViewHolder) viewHolder).viewRightSwipe;
        final View viewLeftSwipe = ((RecyclerAdapterFavorites.MyViewHolder) viewHolder).viewLeftSwipe;

        viewRightSwipe.setVisibility(View.GONE);
        viewLeftSwipe.setVisibility(View.GONE);
        if (dX > 0) {
            viewRightSwipe.setVisibility(View.VISIBLE);
        } else if (dX < 0) {
            viewLeftSwipe.setVisibility(View.VISIBLE);
        }

        getDefaultUIUtil().onDraw(c, recyclerView, viewForeground, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((RecyclerAdapterFavorites.MyViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().clearView(foregroundView);
    }

    public enum SwipeDirection {
        LEFT, RIGHT
    }

    public interface SwipeListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, SwipeDirection direction, int position);
    }
}
