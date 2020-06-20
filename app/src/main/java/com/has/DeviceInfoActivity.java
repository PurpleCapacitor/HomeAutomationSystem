package com.has;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.has.adapters.ActuatorAdapter;
import com.has.adapters.SensorAdapter;
import com.has.async.PopulateActuators;
import com.has.async.PopulateDevices;
import com.has.async.PopulateSensors;
import com.has.data.DatabaseManager;
import com.has.model.Actuator;
import com.has.model.Device;
import com.has.model.Sensor;
import com.has.model.User;

import java.util.ArrayList;
import java.util.List;

public class DeviceInfoActivity extends AppCompatActivity {

    private List<Sensor> sensorList = new ArrayList<>();
    private List<Actuator> actuatorList = new ArrayList<>();
    private DatabaseManager dbManager;
    private Long deviceId;
    private Device device;
    private Long currentUserId;
    private RecyclerView.Adapter sensorAdapter, actuatorAdapter;
    private RecyclerView actuatorRecyclerView;
    private RecyclerView sensorRecyclerView;
    private TextView header, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbManager = new DatabaseManager(getApplicationContext());

        SharedPreferences devicePref = getSharedPreferences("device", 0);
        Gson gson = new Gson();
        String deviceJson = devicePref.getString("device", "");
        device = gson.fromJson(deviceJson, Device.class);
        deviceId = device.getId();
        populateActuators(deviceId);

        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", 0);
        currentUserId = sharedPreferences.getLong("currentUser", 0);

        sensorRecyclerView = findViewById(R.id.recycler_view_devices_info_activity_sensors);
        RecyclerView.LayoutManager sensorLayoutManager = new LinearLayoutManager(this);
        sensorRecyclerView.setLayoutManager(sensorLayoutManager);
        new PopulateSensors(this, sensorRecyclerView).execute(deviceId);
        sensorAdapter = new SensorAdapter(sensorList, this);
        sensorRecyclerView.setAdapter(sensorAdapter);

        actuatorRecyclerView = findViewById(R.id.recycler_view_devices_info_activity_actuators);
        RecyclerView.LayoutManager actuatorLayoutManager = new LinearLayoutManager(this);
        actuatorRecyclerView.setLayoutManager(actuatorLayoutManager);
        new PopulateActuators(this, actuatorRecyclerView).execute(deviceId);
        actuatorAdapter = new ActuatorAdapter(actuatorList, this);
        actuatorRecyclerView.setAdapter(actuatorAdapter);

        Button addSensor = findViewById(R.id.button_add_sensor);
        Button addActuator = findViewById(R.id.button_add_actuator);
        addSensor.setOnClickListener(view -> openAddSensorDialog());
        addActuator.setOnClickListener(view -> openAddActuatorDialog());

        header = findViewById(R.id.text_device_name_info);
        header.setText(device.getName());
        description = findViewById(R.id.text_device_description_info);
        description.setText(device.getDescription());
    }

    private void populateActuators(Long id) {
        actuatorList.clear();
        actuatorList.addAll(dbManager.getActuatorsByDeviceId(id));
    }

    private void populateSenors(Long id) {
        sensorList.clear();
        sensorList.addAll(dbManager.getSensorsByDeviceId(id));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            openEditDeviceDialog();
        } else if (id == R.id.action_delete) {
            dbManager = new DatabaseManager(getApplicationContext());
            dbManager.deleteDevice(deviceId);
            // return back to device list
            Intent intent = new Intent(DeviceInfoActivity.this, MainActivity.class);
            DeviceInfoActivity.this.startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openEditDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_device, null);
        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.edit_device_title);
        EditText deviceNameEditText = view.findViewById(R.id.text_device_name);
        deviceNameEditText.setText(device.getName());
        EditText deviceDescEditText = view.findViewById(R.id.text_device_description);
        deviceDescEditText.setText(device.getDescription());
        builder.setView(view)
                .setNegativeButton(R.string.button_cancel, (dialogInterface, i) -> {})
                .setPositiveButton(R.string.button_edit, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String deviceName = deviceNameEditText.getText().toString();
            String deviceDesc = deviceDescEditText.getText().toString();
            if (deviceName.length() != 0 && deviceDesc.length() != 0) {
                dbManager = new DatabaseManager(getApplicationContext());
                dbManager.updateDevice(deviceId, deviceName, deviceDesc, System.currentTimeMillis(), currentUserId);
                header.setText(deviceName);
                description.setText(deviceDesc);
                //for multiple edits
                device.setName(deviceName);
                device.setDescription(deviceDesc);
                // instant updates for header and description
                SharedPreferences sharedPreferences = getSharedPreferences("device", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(device);
                editor.putString("device", json);
                editor.apply();
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), R.string.fill_device_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddSensorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_sensor_actuator, null);

        EditText deviceNameEditText = view.findViewById(R.id.text_device_name);
        EditText deviceDescEditText = view.findViewById(R.id.text_device_description);
        EditText deviceValueEditText = view.findViewById(R.id.text_device_value);
        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.add_sensor);
        builder.setView(view)
                .setNegativeButton(R.string.button_cancel, (dialogInterface, i) -> {})
                .setPositiveButton(R.string.button_add, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = deviceNameEditText.getText().toString();
            String description = deviceDescEditText.getText().toString();
            String value = deviceValueEditText.getText().toString();
            if (name.length() != 0 && description.length() != 0 && value.length() != 0) {
                dbManager.addSensor(name, description, deviceId, value, System.currentTimeMillis());

                //refresh sensor data
                new PopulateSensors(this, sensorRecyclerView).execute(deviceId);
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), R.string.fill_sensor_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddActuatorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_sensor_actuator, null);

        EditText deviceNameEditText = view.findViewById(R.id.text_device_name);
        EditText deviceDescEditText = view.findViewById(R.id.text_device_description);
        EditText deviceValueEditText = view.findViewById(R.id.text_device_value);
        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.add_actuator);
        builder.setView(view)
                .setNegativeButton(R.string.button_cancel, (dialogInterface, i) -> {})
                .setPositiveButton(R.string.button_add, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = deviceNameEditText.getText().toString();
            String description = deviceDescEditText.getText().toString();
            String value = deviceValueEditText.getText().toString();
            if (name.length() != 0 && description.length() != 0 && value.length() != 0) {
                dbManager.addActuator(name, description, deviceId, value, System.currentTimeMillis());

                //refresh actuator data
                new PopulateActuators(this, actuatorRecyclerView).execute(deviceId);
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), R.string.fill_actuator_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
