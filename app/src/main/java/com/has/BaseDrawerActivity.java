package com.has;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.has.settings.SettingsActivity;
import com.has.settings.UserProfileActivity;
import com.has.ui.ThemeHelper;
import com.has.user.LoginActivity;

public class BaseDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawer;
    public NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_activity1) {
            startAnimatedActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else if (id == R.id.nav_activity2) {
            startAnimatedActivity(new Intent(getApplicationContext(), RulesActivity.class));
        } else if (id == R.id.nav_activity3) {
        startAnimatedActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
        } else if(id == R.id.nav_activity5) {
            startAnimatedActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            startAnimatedActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    protected void startAnimatedActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
