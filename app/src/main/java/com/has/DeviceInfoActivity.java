package com.has;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.ActuatorAdapter;
import com.has.adapters.SensorAdapter;
import com.has.model.Actuator;
import com.has.model.Sensor;

import java.util.ArrayList;
import java.util.List;

public class DeviceInfoActivity extends AppCompatActivity {

    private List<Sensor> sensorList = new ArrayList<>();
    private List<Actuator> actuatorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView sensorRecyclerView = findViewById(R.id.recycler_view_devices_info_activity_sensors);
        populateSenors();
        RecyclerView.Adapter sensorAdapter = new SensorAdapter(sensorList, this);
        sensorRecyclerView.setAdapter(sensorAdapter);
        RecyclerView.LayoutManager sensorLayoutManager = new LinearLayoutManager(this);
        sensorRecyclerView.setLayoutManager(sensorLayoutManager);

        RecyclerView actuatorRecyclerView = findViewById(R.id.recycler_view_devices_info_activity_actuators);
        populateActuators();
        RecyclerView.Adapter actuatorAdapter = new ActuatorAdapter(actuatorList, this);
        actuatorRecyclerView.setAdapter(actuatorAdapter);
        RecyclerView.LayoutManager actuatorLayoutManager = new LinearLayoutManager(this);
        actuatorRecyclerView.setLayoutManager(actuatorLayoutManager);

        FloatingActionButton addButton = findViewById(R.id.floating_button_add);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked add", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateActuators() {
        Actuator a1 = new Actuator("Actuator 1", "Description 1");
        Actuator a2 = new Actuator("Actuator 2", "Description 2");
        actuatorList.add(a1);
        actuatorList.add(a2);
    }

    private void populateSenors() {
        Sensor s1 = new Sensor("Sensor 1", "Description 1");
        Sensor s2 = new Sensor("Sensor 2", "Description 2");
        sensorList.add(s1);
        sensorList.add(s2);
    }

}
