package com.has.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.has.BaseDrawerActivity;
import com.has.R;
import com.has.ui.ThemeHelper;


public class SettingsActivity extends BaseDrawerActivity {

    protected RadioGroup rGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        //TODO kad se okrene landscape ?!
        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity4);

        Button changeLang = findViewById(R.id.button_change_language);
        changeLang.setOnClickListener(v -> {
            Intent i = new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS);
            startActivity(i);
        });

        rGroup = findViewById(R.id.radioGroup);
        int currentTheme = ThemeHelper.getCurrentTheme(this);
        switch (currentTheme) {
            case ThemeHelper.LIGHT_THEME:
                rGroup.check(R.id.radio_light_theme);
                break;
            case ThemeHelper.DARK_THEME:
                rGroup.check(R.id.radio_dark_theme);
                break;
        }
        rGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_light_theme:
                    ThemeHelper.changeTheme(this, ThemeHelper.LIGHT_THEME);
                    break;
                case R.id.radio_dark_theme:
                    ThemeHelper.changeTheme(this, ThemeHelper.DARK_THEME);
                    break;
            }
        });
    }
}
