package com.has;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.DeviceAdapter;
import com.has.async.PopulateDevices;
import com.has.data.DatabaseManager;
import com.has.model.Device;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseDrawerActivity {

    private List<Device> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_main, null, false);

        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity1);
        this.deleteDatabase("HomeAutomation.db"); //TODO za testiranje
        DatabaseManager dbManager = new DatabaseManager(getApplicationContext());
        Device d = new Device(1L, "Klima", "dada", System.currentTimeMillis());
        dbManager.addDeviceAndroid(d,  1L);

        RecyclerView deviceRecyclerView = findViewById(R.id.recycler_view_devices_main_activity);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        deviceRecyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        Long currentUserId = intent.getLongExtra("currentUser", -1L);

        new PopulateDevices(this, deviceRecyclerView).execute(currentUserId);
        RecyclerView.Adapter deviceAdapter = new DeviceAdapter(deviceList, this);
        deviceRecyclerView.setAdapter(deviceAdapter);


        FloatingActionButton addButton = findViewById(R.id.floating_button_add);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked add", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private static class ConnectionTest extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MainActivity.hostAvailable();
        }
    }


    public static boolean hostAvailable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("http://localhost", 8080), 2000);
            return true;
        } catch (IOException e) {
            Log.d("Connection FAILED", e.getMessage());
            return false;
        }
    }
}
