package com.flekapp.lnuc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.util.ImageManager;
import com.flekapp.lnuc.util.SettingsManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ListAdapterFavorites extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SimpleDateFormat mDateFormat;
    private ImageManager imageManager;

    private List<Novel> mNovels;

    public ListAdapterFavorites(Context context, List<Novel> novels) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDateFormat = new SimpleDateFormat(context.getResources().getString(R.string.date_time_format), Locale.getDefault());
        imageManager = new ImageManager(context);

        mNovels = novels;
    }

    @Override
    public int getCount() {
        return mNovels.size();
    }

    public Novel getFavorite(int position) {
        return ((Novel) getItem(position));
    }

    @Override
    public Object getItem(int position) {
        return mNovels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_favorites, parent, false);
        }

        Novel novel = getFavorite(position);

        String imageUrl = novel.getImageUrl();
        Bitmap image = imageManager.getImage(imageUrl);
        if (image != null) {
            ((ImageView) view.findViewById(R.id.list_item_novel_image))
                    .setImageBitmap(image);
        } else {
            ((ImageView) view.findViewById(R.id.list_item_novel_image))
                    .setImageResource(R.mipmap.ic_launcher_round);
        }
        ((TextView) view.findViewById(R.id.list_item_novel_short_name))
                .setText(novel.getShortName());
        ((TextView) view.findViewById(R.id.list_item_novel_name))
                .setText(novel.getName());
        ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_number))
                .setText(novel.getLastChapterNumber());
        ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_title))
                .setText(novel.getLastChapterTitle());
        ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_release))
                .setText(mDateFormat.format(novel.getLastUpdate()));

        // TODO make it normal...
        String currentTheme = SettingsManager.getSettings().getApplicationTheme();
        if (currentTheme == null) {
            currentTheme = "Dark";
        }
        switch (currentTheme) {
            case "Dark":
                ((TextView) view.findViewById(R.id.list_item_novel_short_name)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryDark));
                ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_number)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryDark));
                ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_title)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorPrimaryDark));
                ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_release)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryDark));
                break;
            case "Light":
                ((TextView) view.findViewById(R.id.list_item_novel_short_name)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryLight));
                ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_number)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryLight));
                ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_title)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorPrimaryLight));
                ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_release)).
                        setTextColor(mContext.getResources().getColor(R.color.textColorSecondaryLight));
                break;
        }

        return view;
    }

    public void setNovels(List<Novel> novels) {
        mNovels = novels;
    }
}
