package com.flekapp.lnuc.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.flekapp.lnuc.util.SettingsManager;

import java.util.Timer;
import java.util.TimerTask;

public class RefreshService extends Service {
    private Timer mTimer = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        SettingsManager.Settings settings = SettingsManager.getSettings();
        if (settings.isBackgroundTaskEnable()) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (isDeviceOnline()) {
                        new Refresher(RefreshService.this).startRefreshFavorites();
                    }
                }
            }, 0, settings.getBackgroundRefreshInterval());

            return START_STICKY;
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isDeviceOnline() {
        // TODO send socket request to server for check online status...
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
        return false;
    }
}