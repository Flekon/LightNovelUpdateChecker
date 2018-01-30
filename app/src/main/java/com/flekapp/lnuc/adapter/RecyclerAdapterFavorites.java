package com.flekapp.lnuc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.util.ImageManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapterFavorites extends RecyclerView.Adapter<RecyclerAdapterFavorites.MyViewHolder> {
    public interface OnMoreButtonClickListener {
        void onClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView novelImage;
        TextView shortName, name, chapterNumber, chapterTitle, chapterPublished;
        ImageButton moreImageButton;

        View viewForeground, viewRightSwipe, viewLeftSwipe;

        MyViewHolder(View view) {
            super(view);
            novelImage = view.findViewById(R.id.list_item_novel_image);
            shortName = view.findViewById(R.id.list_item_novel_short_name);
            name = view.findViewById(R.id.list_item_novel_name);
            chapterNumber = view.findViewById(R.id.list_item_novel_last_chapter_number);
            chapterTitle = view.findViewById(R.id.list_item_novel_last_chapter_title);
            chapterPublished = view.findViewById(R.id.list_item_novel_last_chapter_published);
            moreImageButton = view.findViewById(R.id.list_item_novel_button_more);

            viewForeground = view.findViewById(R.id.list_item_favorites_foreground_view);
            viewRightSwipe = view.findViewById(R.id.list_item_favorites_refresh_view);
            viewLeftSwipe = view.findViewById(R.id.list_item_favorites_delete_view);
        }
    }

    private SimpleDateFormat mDateFormat;
    private ImageManager imageManager;
    private List<Novel> mNovels;
    private OnMoreButtonClickListener onMoreButtonClickListener;

    public RecyclerAdapterFavorites(Context context, List<Novel> novels) {
        mDateFormat = new SimpleDateFormat(context.getResources().getString(R.string.date_time_format), Locale.getDefault());
        imageManager = new ImageManager(context);
        mNovels = novels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_favorites, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Novel novel = mNovels.get(position);

        String imageUrl = novel.getImageUrl();
        Bitmap image = imageManager.getImage(imageUrl);
        if (image != null) {
            holder.novelImage.setImageBitmap(image);
        } else {
            holder.novelImage.setImageResource(R.mipmap.ic_launcher_round);
        }
        holder.shortName.setText(novel.getShortName());
        holder.name.setText(novel.getName());
        if (novel.getLastUpdate() == null) {
            holder.chapterNumber.setText("");
            holder.chapterTitle.setText("No chapters...");
            holder.chapterPublished.setText("");
        } else {
            holder.chapterNumber.setText(novel.getLastChapterNumber());
            holder.chapterTitle.setText(novel.getLastChapterTitle());
            holder.chapterPublished.setText(mDateFormat.format(novel.getLastUpdate()));
        }

        holder.moreImageButton.setImageResource(R.drawable.ic_more_vert_black_24dp);
        holder.moreImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMoreButtonClickListener != null) {
                    onMoreButtonClickListener.onClick(view, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNovels.size();
    }

    public void setOnMoreButtonClickListener(OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }
}
