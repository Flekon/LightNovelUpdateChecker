package com.flekapp.lnuc.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.util.ImageManager;
import com.flekapp.lnuc.util.SettingsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListAdapterLastUpdates extends BaseAdapter implements Filterable {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SimpleDateFormat mDateFormat;
    private ImageManager imageManager;

    private List<Chapter> mChapters;
    private List<Chapter> mChaptersProtected;
    private LastUpdateFilter mFilter;

    public ListAdapterLastUpdates(Context context, List<Chapter> chapters) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDateFormat = new SimpleDateFormat(context.getResources().getString(R.string.date_time_format), Locale.getDefault());
        imageManager = new ImageManager(context);

        setChapters(chapters);
        mFilter = new LastUpdateFilter();
    }

    @Override
    public int getCount() {
        return mChapters.size();
    }

    public Chapter getChapter(int position) {
        return ((Chapter) getItem(position));
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

        String imageUrl = chapter.getNovel().getImageUrl();
        Bitmap image = imageManager.getImage(imageUrl);
        if (image != null) {
            ((ImageView) view.findViewById(R.id.list_last_update_item_chapter_novel_image))
                    .setImageBitmap(image);
        } else {
            ((ImageView) view.findViewById(R.id.list_last_update_item_chapter_novel_image))
                    .setImageResource(R.mipmap.ic_launcher_round);
        }
        ((TextView) view.findViewById(R.id.list_last_update_item_chapter_number))
                .setText(chapter.getNumber());
        ((TextView) view.findViewById(R.id.list_last_update_item_chapter_title))
                .setText(chapter.getTitle());
        ((TextView) view.findViewById(R.id.list_last_update_item_chapter_novel))
                .setText(chapter.getNovel().getName());
        ((TextView) view.findViewById(R.id.list_last_update_item_chapter_release))
                .setText(mDateFormat.format(chapter.getReleaseDate()));

        // TODO make it normal...
        String currentTheme = SettingsManager.getSettings().getApplicationTheme();
        if (currentTheme == null) {
            currentTheme = "Dark";
        }
        switch (currentTheme) {
            case "Dark":
                ((TextView) view.findViewById(R.id.list_last_update_item_chapter_title)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorPrimaryDark));
                ((TextView) view.findViewById(R.id.list_last_update_item_chapter_novel)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryDark));
                ((TextView) view.findViewById(R.id.list_last_update_item_chapter_release)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryDark));
                break;
            case "Light":
                ((TextView) view.findViewById(R.id.list_last_update_item_chapter_title)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorPrimaryLight));
                ((TextView) view.findViewById(R.id.list_last_update_item_chapter_novel)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryLight));
                ((TextView) view.findViewById(R.id.list_last_update_item_chapter_release)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryLight));
                break;
        }

        return view;
    }

    private void setChapters(List<Chapter> chapters) {
        mChaptersProtected = new ArrayList<>(chapters);
        mChapters = chapters;
    }

    public void setChapters(List<Chapter> chapters, CharSequence filter) {
        mChaptersProtected = new ArrayList<>(chapters);
        mChapters = filterChapter(chapters, filter);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private List<Chapter> filterChapter(List<Chapter> chapters, CharSequence filter) {
        Resources resources = mContext.getResources();

        List<Chapter> result = new ArrayList<>();
        if (filter == null ||
                filter.equals(resources.getString(R.string.last_update_filters_all))){
            result = chapters;
        } else {
            if (filter.equals(resources.getString(R.string.last_update_filters_today))) {
                for (Chapter chapter : mChaptersProtected) {
                    if (DateUtils.isToday(chapter.getReleaseDate().getTime())) {
                        result.add(chapter);
                    }
                }
            }
        }

        return result;
    }

    public class LastUpdateFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence filter) {
            FilterResults result = new FilterResults();
            List<Chapter> filteredChapters = filterChapter(mChaptersProtected, filter);
            result.values = filteredChapters;
            result.count = filteredChapters.size();

            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mChapters = (ArrayList<Chapter>) results.values;
            notifyDataSetChanged();
        }
    }
}