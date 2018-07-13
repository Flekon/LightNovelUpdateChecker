package com.flekapp.lnuc.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.flekapp.lnuc.MainActivity;
import com.flekapp.lnuc.R;
import com.flekapp.lnuc.data.NovelsRepository;
import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.data.entity.Source;
import com.flekapp.lnuc.data.source.NovelSource;
import com.flekapp.lnuc.data.source.NovelSourceFactory;
import com.flekapp.lnuc.util.ImageManager;
import com.flekapp.lnuc.util.NotifyManager;
import com.flekapp.lnuc.util.SettingsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Refresher {
    public interface OnChaptersRefreshListener {
        void onStartRefreshNovel(Novel novel);
        void onRefreshed(List<Chapter> newChapters);
    }

    public interface OnNovelsRefreshListener {
        void onRefreshed(List<Novel> novels);
    }

    private boolean mBackgroundWorking;
    private Context mContext;
    private NotifyManager mNotifyManager;

    public Refresher(Context context) {
        mBackgroundWorking = false;
        mContext = context;
        mNotifyManager = new NotifyManager(mContext);
    }

    public void startRefreshFavorites() {
        startRefreshFavorite(null);
    }

    public void startRefreshFavorite(Novel novel) {
        final int notificationId = mNotifyManager.show(
                mNotifyManager.create("Check new chapters...", "", false)
                        .setProgress(0, 0, true)
                        .setOngoing(true)
                        .build());

        final List<Novel> novels;
        if (novel == null) {
            novels = new ArrayList<>(NovelsRepository.getFavoritesFromDB(mContext).values());
        } else {
            novels = new ArrayList<>();
            novels.add(novel);
        }

        refreshFavorites(novels, new OnChaptersRefreshListener() {
            @Override
            public void onStartRefreshNovel(Novel novel) {
                Bitmap largeIcon = new ImageManager(mContext).getImage(novel.getImageUrl());
                mNotifyManager.show(notificationId,
                        mNotifyManager.create("Check new chapters...", novel.getName(), false)
                                .setLargeIcon(largeIcon)
                                .setProgress(novels.size(), novels.indexOf(novel), false)
                                .setOngoing(true)
                                .build());
            }

            @Override
            public void onRefreshed(final List<Chapter> newChapters) {
                mNotifyManager.cancel(notificationId);
                SettingsManager.setValue(SettingsManager.VALUES_LAST_UPDATE_DATE, new Date());
                int newChaptersCount = newChapters.size();
                if (newChaptersCount > 0) {
                    if (newChaptersCount < 4) {
                        for (Chapter chapter : newChapters) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chapter.getUrl()));
                            PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

                            String text = String.format("(%s) [%s] %s",
                                    chapter.getNovel().getShortName(), chapter.getNumber(), chapter.getTitle());

                            Bitmap largeIcon = new ImageManager(mContext).getImage(chapter.getNovel().getImageUrl());
                            mNotifyManager.show(
                                    mNotifyManager.create(mContext.getResources().getString(R.string.notification_new_chapter), text)
                                            .setWhen(chapter.getPublicationDate().getTime())
                                            .setLargeIcon(largeIcon)
                                            .setContentIntent(pIntent)
                                            .build());
                        }
                    } else {
                        Collections.reverse(newChapters);

                        Intent intent = new Intent(mContext, MainActivity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

                        String text = "New chapters count is " + newChaptersCount;
                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                        inboxStyle.setBigContentTitle("List of published chapters:");
                        for (Chapter chapter : newChapters) {
                            inboxStyle.addLine(String.format("(%s) [%s] %s",
                                    chapter.getNovel().getShortName(), chapter.getNumber(), chapter.getTitle()));
                        }

                        mNotifyManager.show(
                                mNotifyManager.create(text, "...")
                                        .setNumber(newChaptersCount)
                                        .setStyle(inboxStyle)
                                        .setContentIntent(pIntent)
                                        .build());
                    }
                }
            }
        });
    }

    private void refreshFavorites(@NonNull final List<Novel> novels, @NonNull final OnChaptersRefreshListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBackgroundWorking = true;
                List<Chapter> newChapters = new ArrayList<>();
                for (Novel novel : novels) {
                    listener.onStartRefreshNovel(novel);
                    NovelSource source = NovelSourceFactory.getSource(novel.getSource());
                    if (source != null) {
                        List<Chapter> chapters = source.getLastChapters(novel);
                        // TODO update novel status !
                        if (novel.getStatus() == Novel.Status.ONGOING) {
                            Collections.reverse(chapters);

                            List<Chapter> newNovelChapters = new ArrayList<>();
                            for (Chapter chapter : chapters) {
                                if (novel.getLastUpdate() == null ||
                                        novel.getLastUpdate().before(chapter.getPublicationDate())) {
                                    if (NovelsRepository.addChapter(mContext, chapter)) {
                                        newNovelChapters.add(chapter);
                                    }
                                }
                            }

                            newChapters.addAll(newNovelChapters);
                        }
                    }
                }
                mBackgroundWorking = false;

                listener.onRefreshed(newChapters);
            }
        }).start();
    }

    public void refreshNovels(@NonNull final Source source, @NonNull final OnNovelsRefreshListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBackgroundWorking = true;
                List<Novel> novels = null;
                NovelSource novelSource = NovelSourceFactory.getSource(source);
                if (novelSource != null) {
                    novels = novelSource.getNovels();
                }
                mBackgroundWorking = false;

                listener.onRefreshed(novels);
            }
        }).start();
    }

    public boolean isBackgroundWorking() {
        return mBackgroundWorking;
    }
}
