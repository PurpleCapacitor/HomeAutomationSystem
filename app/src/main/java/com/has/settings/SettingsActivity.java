package com.has.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.has.BaseDrawerActivity;
import com.has.R;


public class SettingsActivity extends BaseDrawerActivity {

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

    }
}
