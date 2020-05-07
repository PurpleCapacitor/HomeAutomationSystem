package com.has;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.DeviceAdapter;
import com.has.model.Device;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Device> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView deviceRecyclerView = findViewById(R.id.recycleview_devices_main_activity);
        populateList();
        RecyclerView.Adapter deviceAdapter = new DeviceAdapter(deviceList, this);
        deviceRecyclerView.setAdapter(deviceAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        deviceRecyclerView.setLayoutManager(layoutManager);

        FloatingActionButton addButton = findViewById(R.id.floating_button_add_device);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked add", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //TODO treba search ikonica gore desno i tu da se pojavi dijalog sta da pretrazujes

    private void populateList() {
        deviceList = new ArrayList<>();
        Device d1 = new Device();
        d1.setName("Device 1");
        d1.setDescription("This is a description");
        Device d2 = new Device();
        d2.setName("Device 2");
        d2.setDescription("This is a description");
        deviceList.add(d1);
        deviceList.add(d2);

    }
}
