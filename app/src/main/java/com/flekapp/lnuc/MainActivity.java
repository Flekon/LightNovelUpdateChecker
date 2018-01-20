package com.flekapp.lnuc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.flekapp.lnuc.fragment.FavoriteFragment;
import com.flekapp.lnuc.fragment.LastUpdateFragment;
import com.flekapp.lnuc.fragment.NovelsFragment;
import com.flekapp.lnuc.service.RefreshService;
import com.flekapp.lnuc.service.Refresher;
import com.flekapp.lnuc.util.SettingsManager;

import static android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends ThemedActivity {
    private Fragment mFragment;
    private FragmentManager mFragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // TODO refresh last fragment on resume or enter
            switch (item.getItemId()) {
                case R.id.menu_navigation_last_updates:
                    mFragment = new LastUpdateFragment();
                    break;
                case R.id.menu_navigation_favorite:
                    mFragment = new FavoriteFragment();
                    break;
                case R.id.menu_navigation_novels:
                    mFragment = new NovelsFragment();
                    break;
            }
            final FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.main_activity_frame_container, mFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SettingsManager.isNotInit()) {
            SettingsManager.initSettings(getDefaultSharedPreferences(getApplicationContext()));
            Intent refreshService = new Intent(this, RefreshService.class);
            startService(refreshService);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.menu_navigation_last_updates);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_main_refresh:
                new Refresher(this).startRefreshFavorites();
                return true;
            case R.id.menu_main_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_main_exit:
                finish();
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // TODO remove block
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTINGS:
                SettingsManager.initSettings(getDefaultSharedPreferences(getApplicationContext()));
                Intent refreshService = new Intent(this, RefreshService.class);
                startService(refreshService);
                break;
        }
    }*/
}
