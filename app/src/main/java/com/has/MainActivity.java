package com.has;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.DeviceAdapter;
import com.has.data.DatabaseManager;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.Device;
import com.has.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseDrawerActivity {

    private List<Device> deviceList = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_main, null, false);

        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity1);
        this.deleteDatabase("HomeAutomation.db"); //TODO za testiranje
        dbManager = new DatabaseManager(getApplicationContext());

        dbManager.addDevice("Klima", "Description 1");
        dbManager.addDevice("Roletna", "Description 2");

        RecyclerView deviceRecyclerView = findViewById(R.id.recycler_view_devices_main_activity);
        populateList();
        RecyclerView.Adapter deviceAdapter = new DeviceAdapter(deviceList, this);
        deviceRecyclerView.setAdapter(deviceAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        deviceRecyclerView.setLayoutManager(layoutManager);

        FloatingActionButton addButton = findViewById(R.id.floating_button_add);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked add", Toast.LENGTH_SHORT).show();
            }
        });

        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        //Call<User> call = apiService.getAllUsers();
        //ili
        apiService.getUser().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateList() {
        deviceList = dbManager.getAllDevices();
    }
}
