package com.has;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.ActuatorAdapter;
import com.has.adapters.SensorAdapter;
import com.has.data.DatabaseManager;
import com.has.model.Actuator;
import com.has.model.Device;
import com.has.model.Sensor;
import com.has.settings.UserProfileActivity;
import com.has.user.LoginActivity;
import com.has.user.RegisterActivity;

import java.util.ArrayList;
import java.util.List;

public class DeviceInfoActivity extends AppCompatActivity {

    private List<Sensor> sensorList = new ArrayList<>();
    private List<Actuator> actuatorList = new ArrayList<>();
    private DatabaseManager dbManager;

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
        Long id = intent.getLongExtra("deviceId", -1L);
        populateActuators(id);

        //test
        /*int i = dbManager.updateActuator(1L, "Bex", "NOM", devices.get(0).getId(), "6");
        Log.d("Update int value", "is " + i);
        dbManager.deleteActuator(2L);*/

        RecyclerView sensorRecyclerView = findViewById(R.id.recycler_view_devices_info_activity_sensors);
        populateSenors(id);
        RecyclerView.Adapter sensorAdapter = new SensorAdapter(sensorList, this);
        sensorRecyclerView.setAdapter(sensorAdapter);
        RecyclerView.LayoutManager sensorLayoutManager = new LinearLayoutManager(this);
        sensorRecyclerView.setLayoutManager(sensorLayoutManager);

        RecyclerView actuatorRecyclerView = findViewById(R.id.recycler_view_devices_info_activity_actuators);
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

    private void populateActuators(Long id) {
       actuatorList = dbManager.getActuatorsByDeviceId(id);
    }

    private void populateSenors(Long id) {
        sensorList = dbManager.getSensorsByDeviceId(id);
    }

}
