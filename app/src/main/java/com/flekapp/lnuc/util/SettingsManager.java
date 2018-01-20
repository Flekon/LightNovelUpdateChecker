package com.flekapp.lnuc.util;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    public static final String PREFERENCES_APPLICATION_THEME = "setting.application.theme";
    public static final String PREFERENCES_BACKGROUND_TASK_ENABLE = "setting.background_task.enable";
    public static final String PREFERENCES_BACKGROUND_TASK_REFRESH_INTERVAL = "setting.background_task.refresh_interval";
    public static final String PREFERENCES_NOTIFICATION_VIBRATE_ENABLE = "setting.notification.vibrate_enable";
    public static final String PREFERENCES_NOTIFICATION_LIGHTS_ENABLE = "setting.notification.lights_enable";
    public static final String PREFERENCES_NOTIFICATION_SOUND_ENABLE = "setting.notification.sound_enable";

    public static final String VALUES_LAST_UPDATE_DATE = "values.last_update_date";

    private static final String DEFAULT_APPLICATION_THEME = null;
    private static final boolean DEFAULT_BACKGROUND_TASK_ENABLE = false;
    private static final String  DEFAULT_BACKGROUND_TASK_REFRESH_INTERVAL = "3600000"; // 1 hour
    private static final boolean DEFAULT_NOTIFICATION_VIBRATE_ENABLE = true;
    private static final boolean DEFAULT_NOTIFICATION_LIGHTS_ENABLE = true;
    private static final boolean DEFAULT_NOTIFICATION_SOUND_ENABLE = true;

    public static class Settings {
        String applicationTheme;
        boolean isBackgroundTaskEnable;
        long backgroundRefreshInterval;
        boolean isNotificationVibrateEnable;
        boolean isNotificationLightsEnable;
        boolean isNotificationSoundEnable;

        public String getApplicationTheme() {
            return applicationTheme;
        }

        public void refreshApplicationTheme() {
            applicationTheme = sSharedPreferences
                    .getString(PREFERENCES_APPLICATION_THEME,
                        DEFAULT_APPLICATION_THEME);
        }

        public boolean isBackgroundTaskEnable() {
            return isBackgroundTaskEnable;
        }

        public void refreshBackgroundTaskEnable() {
            isBackgroundTaskEnable = sSharedPreferences
                    .getBoolean(PREFERENCES_BACKGROUND_TASK_ENABLE,
                        DEFAULT_BACKGROUND_TASK_ENABLE);
        }

        public long getBackgroundRefreshInterval() {
            return backgroundRefreshInterval;
        }

        public void refreshBackgroundRefreshInterval() {
            backgroundRefreshInterval = Long.parseLong(sSharedPreferences
                    .getString(PREFERENCES_BACKGROUND_TASK_REFRESH_INTERVAL,
                        DEFAULT_BACKGROUND_TASK_REFRESH_INTERVAL));
        }

        public boolean isNotificationVibrateEnable() {
            return isNotificationVibrateEnable;
        }

        public void refreshNotificationVibrateEnable() {
            isNotificationVibrateEnable = sSharedPreferences
                    .getBoolean(PREFERENCES_NOTIFICATION_VIBRATE_ENABLE,
                        DEFAULT_NOTIFICATION_VIBRATE_ENABLE);
        }

        public boolean isNotificationLightsEnable() {
            return isNotificationLightsEnable;
        }

        public void refreshNotificationLightsEnable() {
            isNotificationLightsEnable = sSharedPreferences
                    .getBoolean(PREFERENCES_NOTIFICATION_LIGHTS_ENABLE,
                        DEFAULT_NOTIFICATION_LIGHTS_ENABLE);
        }

        public boolean isNotificationSoundEnable() {
            return isNotificationSoundEnable;
        }

        public void refreshNotificationSoundEnable() {
            isNotificationSoundEnable = sSharedPreferences
                    .getBoolean(PREFERENCES_NOTIFICATION_SOUND_ENABLE,
                        DEFAULT_NOTIFICATION_SOUND_ENABLE);
        }
    }

    private static SharedPreferences sSharedPreferences;
    private static Settings sSettings;
    private static Map<String, Object> sValues;

    public static boolean isNotInit() {
        return sSettings == null;
    }

    public static void initSettings(SharedPreferences preferences) {
        sSharedPreferences = preferences;

        sSettings = new Settings();
        sSettings.refreshApplicationTheme();
        sSettings.refreshBackgroundTaskEnable();
        sSettings.refreshBackgroundRefreshInterval();
        sSettings.refreshNotificationVibrateEnable();
        sSettings.refreshNotificationLightsEnable();
        sSettings.refreshNotificationSoundEnable();

        sValues = new HashMap<>();
    }
    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static Settings getSettings() {
        return sSettings;
    }

    public static Object getValue(String key) {
        if (sValues != null) {
            return sValues.get(key);
        }

        return null;
    }

    public static void setValue(String key, Object value) {
        if (sValues != null) {
            sValues.put(key, value);
        }
    }
}