package com.flekapp.lnuc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.flekapp.lnuc.fragment.PreferenceSettingsFragment;
import com.flekapp.lnuc.service.RefreshService;
import com.flekapp.lnuc.util.SettingsManager;

public class SettingsActivity extends ThemedActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_preference_container, new PreferenceSettingsFragment())
                .commit();
        SettingsManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i("Setting", "change value: " + key);
        SettingsManager.Settings settings = SettingsManager.getSettings();
        switch (key) {
            case SettingsManager.PREFERENCES_APPLICATION_THEME:
                settings.refreshApplicationTheme();
                break;
            case SettingsManager.PREFERENCES_BACKGROUND_TASK_ENABLE:
                // TODO refresh background service
                settings.refreshBackgroundTaskEnable();
                Intent refreshService = new Intent(this, RefreshService.class);
                startService(refreshService);
                break;
            case SettingsManager.PREFERENCES_BACKGROUND_TASK_REFRESH_INTERVAL:
                // TODO refresh background service
                settings.refreshBackgroundRefreshInterval();
                refreshService = new Intent(this, RefreshService.class);
                startService(refreshService);
                break;
            case SettingsManager.PREFERENCES_NOTIFICATION_VIBRATE_ENABLE:
                settings.refreshNotificationVibrateEnable();
                break;
            case SettingsManager.PREFERENCES_NOTIFICATION_LIGHTS_ENABLE:
                settings.refreshNotificationLightsEnable();
                break;
            case SettingsManager.PREFERENCES_NOTIFICATION_SOUND_ENABLE:
                settings.refreshNotificationSoundEnable();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO remove block
    /*@Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }*/
}
