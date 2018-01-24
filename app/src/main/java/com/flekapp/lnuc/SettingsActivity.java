package com.flekapp.lnuc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

        findViewById(R.id.button_reset_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "reset images caches", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_reset_all_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "reset all data", Toast.LENGTH_SHORT).show();
            }
        });
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
                settings.refreshBackgroundTaskEnable();
                Intent refreshService = new Intent(this, RefreshService.class);
                startService(refreshService);
                break;
            case SettingsManager.PREFERENCES_BACKGROUND_TASK_REFRESH_INTERVAL:
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
}
