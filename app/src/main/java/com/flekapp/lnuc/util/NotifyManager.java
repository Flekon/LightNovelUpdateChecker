package com.flekapp.lnuc.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.flekapp.lnuc.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifyManager {
    private static int sNotificationId = 0;

    private Context mContext;
    private NotificationManager mNotificationManager;

    public NotifyManager(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
    }

    private int getDefaults() {
        SettingsManager.Settings settings = SettingsManager.getSettings();
        int def = new Notification().defaults;
        if (settings.isNotificationVibrateEnable())
            def |= Notification.DEFAULT_VIBRATE;
        if (settings.isNotificationLightsEnable())
            def |= Notification.DEFAULT_LIGHTS;
        if (settings.isNotificationSoundEnable())
            def |= Notification.DEFAULT_SOUND;
        return def;
    }

    public NotificationCompat.Builder create(@NonNull String title, @NonNull String text) {
        return create(title, text, true);
    }

    public NotificationCompat.Builder create(@NonNull String title,
                                             @NonNull String text,
                                             boolean setDefaults) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);

        if (setDefaults) {
            builder.setDefaults(getDefaults());
        }

        return builder;
    }

    public int show(Notification notification) {
        sNotificationId++;
        mNotificationManager.notify(sNotificationId, notification);
        return sNotificationId;
    }

    public void show(int notificationId, Notification notification) {
        mNotificationManager.notify(notificationId, notification);
    }

    public void cancel(int notificationId) {
        mNotificationManager.cancel(notificationId);
    }
}
