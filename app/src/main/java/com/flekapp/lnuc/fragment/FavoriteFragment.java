package com.flekapp.lnuc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flekapp.lnuc.R;

public class FavoriteFragment extends Fragment {
    public FavoriteFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.menu_navigation_favorite));
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }
}