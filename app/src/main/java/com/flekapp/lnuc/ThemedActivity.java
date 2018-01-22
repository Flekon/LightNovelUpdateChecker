package com.flekapp.lnuc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.flekapp.lnuc.util.SettingsManager;

public class ThemedActivity extends AppCompatActivity {
    private String currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int theme;
        currentTheme = SettingsManager.getSettings().getApplicationTheme();
        if (currentTheme == null) {
            currentTheme = "Dark";
        }
        switch (currentTheme) {
            case "Light":
                theme = R.style.ApplicationLightTheme;
                break;
            case "Dark":
            default:
                theme = R.style.ApplicationDarkTheme;
                break;
        }
        setTheme(theme);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentTheme();
    }

    protected void checkCurrentTheme() {
        String newTheme = SettingsManager.getSettings().getApplicationTheme();
        if (newTheme != null && !currentTheme.equals(newTheme)) {
            recreate();
        }
    }
}
