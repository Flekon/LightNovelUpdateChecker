package com.flekapp.lnuc.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.flekapp.lnuc.R;
import com.flekapp.lnuc.data.NovelsRepository;
import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.data.entity.Source;
import com.flekapp.lnuc.data.source.NovelSource;
import com.flekapp.lnuc.data.source.NovelSourceFactory;
import com.flekapp.lnuc.util.SettingsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Refresher {
    public interface onFavoritesRefreshed {
        void onRefresh(List<Chapter> newChapters);
    }

    public interface onNovelsRefreshed {
        void onRefresh(List<Novel> novels);
    }

    private static final String TAG = Refresher.class.getSimpleName();

    private static int sNotificationId = 0;

    private Context mContext;
    private NotificationManager mNotificationManager;

    public Refresher(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
    }

    public void startRefreshFavorites() {
        final int notificationId = ++sNotificationId;

        SettingsManager.Settings settings = SettingsManager.getSettings();
        int def = new Notification().defaults;
        if (settings.isNotificationVibrateEnable())
            def |= Notification.DEFAULT_VIBRATE;
        if (settings.isNotificationLightsEnable())
            def |= Notification.DEFAULT_LIGHTS;
        if (settings.isNotificationSoundEnable())
            def |= Notification.DEFAULT_SOUND;
        final int defaults = def;

        // TODO make normal notification
        final Notification n = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(mContext.getResources().getString(R.string.app_name))
                .setContentText("Check new chapters...")
                .setProgress(0, 0, true)
                .build();
        mNotificationManager.notify(notificationId, n);

        refreshFavorites(new Refresher.onFavoritesRefreshed() {
            @Override
            public void onRefresh(final List<Chapter> newChapters) {
                SettingsManager.setValue(SettingsManager.VALUES_LAST_UPDATE_DATE, new Date());
                int newChaptersCount = newChapters.size();
                if (newChaptersCount > 0) {
                    if (newChaptersCount < 4) {
                        int nId = notificationId;
                        for (Chapter chapter : newChapters) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chapter.getUrl()));
                            PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

                            String text = String.format("(%s) [%s] %s",
                                    chapter.getNovel().getShortName(), chapter.getNumber(), chapter.getTitle());

                            final Notification n = new NotificationCompat.Builder(mContext)
                                    .setSmallIcon(R.drawable.ic_launcher_background)
                                    .setContentTitle(mContext.getResources().getString(R.string.notification_new_chapter))
                                    .setContentText(text)
                                    .setContentIntent(pIntent)
                                    .setAutoCancel(true)
                                    .setDefaults(defaults)
                                    .build();

                            mNotificationManager.notify(nId, n);
                            nId = ++sNotificationId;
                        }
                    } else {
                        Collections.reverse(newChapters);

                        String text = "New chapters count is " + newChaptersCount;
                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                        inboxStyle.setBigContentTitle("List of released chapters:");
                        for (Chapter chapter : newChapters) {
                            inboxStyle.addLine(String.format("(%s) [%s] %s",
                                    chapter.getNovel().getShortName(), chapter.getNumber(), chapter.getTitle()));
                        }

                        final Notification n = new NotificationCompat.Builder(mContext)
                                .setNumber(newChaptersCount)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle(text)
                                .setContentText("...")
                                .setStyle(inboxStyle)
                                .setAutoCancel(true)
                                .setDefaults(defaults)
                                .build();
                        mNotificationManager.notify(notificationId, n);
                    }
                } else {
                    mNotificationManager.cancel(notificationId);
                    /*final Notification n = new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(mContext.getResources().getString(R.string.app_name))
                            .setContentText("No updates found")
                            .setAutoCancel(true)
                            .build();
                    mNotificationManager.notify(notificationId, n);*/
                }
            }
        });
    }

    private void refreshFavorites(@NonNull final onFavoritesRefreshed listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Chapter> newChapters = new ArrayList<>();
                List<Novel> novels = new ArrayList<>(NovelsRepository.getFavoritesFromDB(mContext).values());
                for (Novel novel : novels) {
                    NovelSource source = NovelSourceFactory.getSource(novel.getSource());
                    if (source != null) {
                        List<Chapter> chapters = source.getLastChapters(novel);
                        Collections.reverse(chapters);

                        List<Chapter> newNovelChapters = new ArrayList<>();
                        for (Chapter chapter : chapters) {
                            if (novel.getLastUpdate() == null ||
                                    novel.getLastUpdate().before(chapter.getReleaseDate())) {
                                newNovelChapters.add(chapter);
                                NovelsRepository.addChapter(mContext, chapter);
                            }
                        }

                        newChapters.addAll(newNovelChapters);
                    }
                }

                listener.onRefresh(newChapters);
            }
        }).start();
    }

    public void refreshNovels(@NonNull final Source source, @NonNull final onNovelsRefreshed listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Novel> novels = null;
                NovelSource novelSource = NovelSourceFactory.getSource(source);
                if (novelSource != null) {
                    novels = novelSource.getNovels();
                }

                listener.onRefresh(novels);
            }
        }).start();
    }
}
