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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.adapter.ListAdapterLastUpdates;
import com.flekapp.lnuc.data.NovelsRepository;
import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.util.SettingsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LastUpdateFragment extends Fragment {
    private Spinner mFilterSpinner;
    private TextView mTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;

    private ListAdapterLastUpdates mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.menu_navigation_last_updates));
        View view = inflater.inflate(R.layout.fragment_last_update, container, false);

        mFilterSpinner = view.findViewById(R.id.last_update_spinner_filter);
        mTextView = view.findViewById(R.id.last_update_date_value);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout_list_last_update);
        mListView = view.findViewById(R.id.list_last_update);
        mListView.setEmptyView(view.findViewById(R.id.list_last_update_empty_text));

        initFilterSpinner();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragmentContent();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAdapter.getChapter(position).getUrl()));
                startActivity(intent);
            }
        });

        mAdapter = new ListAdapterLastUpdates(getActivity().getApplicationContext(), new ArrayList<Chapter>());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSwipeRefreshLayout.setRefreshing(true);
        refreshFragmentContent();
    }

    private void initFilterSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.lastUpdateFiltersValue, R.layout.spinner_last_update_filter_item);
        adapter.setDropDownViewResource(R.layout.spinner_last_update_filter_dropdown_item);

        mFilterSpinner.setAdapter(adapter);
        mFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.getFilter().filter(String.valueOf(parent.getItemAtPosition(position)));
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void refreshFragmentContent() {
        Date lastUpdate = (Date) SettingsManager.getValue(SettingsManager.VALUES_LAST_UPDATE_DATE);
        if (mTextView != null && lastUpdate != null) {
            String date = new SimpleDateFormat(getResources().getString(R.string.date_time_format), Locale.getDefault()).format(lastUpdate);
            mTextView.setText(date);
        }

        List<Chapter> chapters = new ArrayList<>(NovelsRepository
                .getChaptersFromDB(getActivity().getApplicationContext()).values());
        mAdapter.setChapters(chapters, String.valueOf(mFilterSpinner.getSelectedItem()));
        mListView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}