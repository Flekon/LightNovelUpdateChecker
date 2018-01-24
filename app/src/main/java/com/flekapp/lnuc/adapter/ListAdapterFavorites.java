package com.flekapp.lnuc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.service.Refresher;
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

        final Novel novel = getFavorite(position);

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
        if (novel.getLastUpdate() == null) {
            ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_number))
                    .setText("");
            ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_title))
                    .setText("No chapters...");
            ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_release))
                    .setText("");
        } else {
            ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_number))
                    .setText(novel.getLastChapterNumber());
            ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_title))
                    .setText(novel.getLastChapterTitle());
            ((TextView) view.findViewById(R.id.list_item_novel_last_chapter_release))
                    .setText(mDateFormat.format(novel.getLastUpdate()));
        }

        final ImageView moreView = view.findViewById(R.id.list_item_novel_button_more);
        moreView.setImageResource(R.drawable.ic_more_vert_black_24dp);
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFavoritesPopupMenu(view, novel);
            }
        });

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

    private void showFavoritesPopupMenu(View view, final Novel novel) {
        PopupMenu menu = new PopupMenu(mContext, view);
        menu.inflate(R.menu.menu_popup_favorites);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_popup_favorites_refresh:
                        new Refresher(mContext).startRefreshFavorite(novel);
                        return true;
                    case R.id.menu_popup_favorites_remove:
                        Toast.makeText(mContext, "Remove " + novel.getName(), Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        menu.show();
    }
}
