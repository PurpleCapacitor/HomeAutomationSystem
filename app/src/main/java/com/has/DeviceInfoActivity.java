package com.has;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.has.adapters.ActuatorAdapter;
import com.has.adapters.SensorAdapter;
import com.has.data.DatabaseManager;
import com.has.model.Actuator;
import com.has.model.Device;
import com.has.model.Sensor;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbManager = new DatabaseManager(getApplicationContext());
        List<Device> devices = dbManager.getAllDevices();
        dbManager.addActuator("Act", "bla", devices.get(0).getId(), "5");
        dbManager.addActuator("Hex", "hjdaskjdsa", devices.get(0).getId(), "6");
        dbManager.addSensor("Temp", "temperature", devices.get(0).getId(), "5", System.currentTimeMillis());
        dbManager.addSensor("Humidity", "humidity", devices.get(0).getId(), "60%", System.currentTimeMillis());

        Intent intent = getIntent();
        device = (Device)intent.getSerializableExtra("device");
        deviceId = device.getId();
        populateActuators(deviceId);

        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", 0);
        currentUserId = sharedPreferences.getLong("currentUser", 0);

        RecyclerView sensorRecyclerView = findViewById(R.id.recycler_view_devices_info_activity_sensors);
        populateSenors(deviceId);
        sensorAdapter = new SensorAdapter(sensorList, this);
        sensorRecyclerView.setAdapter(sensorAdapter);
        RecyclerView.LayoutManager sensorLayoutManager = new LinearLayoutManager(this);
        sensorRecyclerView.setLayoutManager(sensorLayoutManager);

        RecyclerView actuatorRecyclerView = findViewById(R.id.recycler_view_devices_info_activity_actuators);
        actuatorAdapter = new ActuatorAdapter(actuatorList, this);
        actuatorRecyclerView.setAdapter(actuatorAdapter);
        RecyclerView.LayoutManager actuatorLayoutManager = new LinearLayoutManager(this);
        actuatorRecyclerView.setLayoutManager(actuatorLayoutManager);

        Button addSensor = findViewById(R.id.button_add_sensor);
        Button addActuator = findViewById(R.id.button_add_actuator);
        addSensor.setOnClickListener(view -> openAddSensorDialog());
        addActuator.setOnClickListener(view -> openAddActuatorDialog());
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
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "Please fill in device data", Toast.LENGTH_SHORT).show();
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
                populateSenors(deviceId);
                sensorAdapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "Please fill in all sensor data", Toast.LENGTH_SHORT).show();
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
                dbManager.addActuator(name, description, deviceId, value);
                //refresh actuator data
                populateActuators(deviceId);
                actuatorAdapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "Please fill in all actuator data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
