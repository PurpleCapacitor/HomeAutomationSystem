package com.has;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.DeviceAdapter;
import com.has.async.PopulateDevices;
import com.has.data.DatabaseManager;
import com.has.model.Device;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseDrawerActivity {

    private List<Device> deviceList = new ArrayList<>();
    private DatabaseManager dbManager;
    private Long currentUserId;
    private RecyclerView deviceRecyclerView;
    private RecyclerView.Adapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_main, null, false);

        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity1);
        deviceRecyclerView = findViewById(R.id.recycler_view_devices_main_activity);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        deviceRecyclerView.setLayoutManager(layoutManager);

        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", 0);
        currentUserId = sharedPreferences.getLong("currentUser", 0);

        new PopulateDevices(this, deviceRecyclerView).execute(currentUserId);
        deviceAdapter = new DeviceAdapter(deviceList, this);
        deviceRecyclerView.setAdapter(deviceAdapter);

        FloatingActionButton addButton = findViewById(R.id.floating_button_add);
        addButton.setOnClickListener(v -> openAddNewDeviceDialog());
    }

    private void openAddNewDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_device, null);
        EditText deviceNameEditText = view.findViewById(R.id.text_device_name);
        EditText deviceDescEditText = view.findViewById(R.id.text_device_description);
        builder.setView(view)
                .setNegativeButton(getResources().getString(R.string.button_cancel), (dialogInterface, i) -> {
                })
                .setPositiveButton(getResources().getString(R.string.button_add), null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String deviceName = deviceNameEditText.getText().toString();
            String deviceDesc = deviceDescEditText.getText().toString();
            if (deviceName.length() != 0 && deviceDesc.length() != 0) {
                dbManager = new DatabaseManager(getApplicationContext());
                dbManager.addDevice(deviceName, deviceDesc, currentUserId, System.currentTimeMillis());

                // update device display
                new PopulateDevices(this, deviceRecyclerView).execute(currentUserId);
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), R.string.fill_device_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
