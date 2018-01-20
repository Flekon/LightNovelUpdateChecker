package com.flekapp.lnuc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.data.entity.Chapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LastUpdateListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Chapter> mChapters;
    private SimpleDateFormat mDateFormat;

    public LastUpdateListAdapter(Context context, List<Chapter> chapters) {
        mContext = context;
        mChapters = chapters;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDateFormat = new SimpleDateFormat(context.getResources().getString(R.string.date_time_format), Locale.getDefault());
    }

    @Override
    public int getCount() {
        return mChapters.size();
    }

    @Override
    public Object getItem(int position) {
        return mChapters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_last_update, parent, false);
        }

        Chapter chapter = getChapter(position);

        ((TextView) view.findViewById(R.id.list_last_update_item_chapter_number))
                .setText(chapter.getNumber());
        ((TextView) view.findViewById(R.id.list_last_update_item_chapter_title))
                .setText(chapter.getTitle());
        ((TextView) view.findViewById(R.id.list_last_update_item_chapter_novel))
                .setText(chapter.getNovel().getName());
        ((TextView) view.findViewById(R.id.list_last_update_item_chapter_release))
                .setText(mDateFormat.format(chapter.getReleaseDate()));

        return view;
    }

    private Chapter getChapter(int position) {
        return ((Chapter) getItem(position));
    }
}