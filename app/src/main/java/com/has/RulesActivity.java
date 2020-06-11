package com.has;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.RuleAdapter;
import com.has.adapters.SensorAdapter;
import com.has.async.PopulateDevices;
import com.has.async.PopulateRules;
import com.has.data.DatabaseManager;
import com.has.model.Actuator;
import com.has.model.Device;
import com.has.model.Rule;
import com.has.model.Sensor;
import com.has.model.User;

import java.util.ArrayList;
import java.util.List;

public class RulesActivity extends BaseDrawerActivity {

    private List<Rule> ruleList = new ArrayList<>();
    private List<Sensor> sensorList = new ArrayList<>();
    private List<Actuator> actuatorList = new ArrayList<>();
    private List<Device> deviceList = new ArrayList<>();
    private Context context;

    private DatabaseManager dbManager;
    private RecyclerView.Adapter ruleAdapter;
    private RecyclerView.Adapter sensorAdapter;
    private RecyclerView.Adapter actuatorAdapter;

    private Long currentUserId;
    private RecyclerView ruleRecyclerView;
    private Spinner spinnerSensors;
    private Spinner spinnerActuators;

    private Actuator actuator = null;
    private Sensor sensor = null;

    private static final String TAG = "MyActivity";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_rules, null, false);

        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity2);


        ruleRecyclerView = findViewById(R.id.recycler_view_rules_activity_rules);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ruleRecyclerView.setLayoutManager(layoutManager);

        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", 0);
        currentUserId = sharedPreferences.getLong("currentUser", 0);

        //ZA SENSORE I AKTUATORE

        dbManager = new DatabaseManager(getApplicationContext());
        deviceList = dbManager.getDevicesByUserId(currentUserId);

        for(Device d: deviceList)
        {
            List<Sensor> sensors = dbManager.getSensorsByDeviceId(d.getId());

            for(Sensor s: sensors)
            {
                sensorList.add(s);
            }

            List<Actuator> actuators = dbManager.getActuatorsByDeviceId(d.getId());

            for(Actuator s: actuators)
            {
                actuatorList.add(s);
            }

        }

        new PopulateRules(this, ruleRecyclerView).execute(currentUserId);
        ruleAdapter = new RuleAdapter(ruleList, this);
        ruleRecyclerView.setAdapter(ruleAdapter);

        Log.i(TAG, "RuleList " + ruleList);


        FloatingActionButton addButton = findViewById(R.id.floating_button_add);
        addButton.setOnClickListener(v -> openAddNewRuleDialog());
    }

    private void openAddNewRuleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RulesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_rule, null);


        spinnerSensors = (Spinner) view.findViewById(R.id.rule_spinner_sensors);
        ArrayAdapter<Sensor> sensorArrayAdapter = new ArrayAdapter<Sensor>(this,android.R.layout.simple_spinner_item,sensorList);
        sensorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSensors.setAdapter(sensorArrayAdapter);

        spinnerSensors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             sensor = (Sensor) parent.getSelectedItem();
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {

         }
     });

                spinnerActuators = (Spinner) view.findViewById(R.id.rule_spinner_actuators);
        ArrayAdapter<Actuator> actuatorArrayAdapter = new ArrayAdapter<Actuator>(this,android.R.layout.simple_spinner_item,actuatorList);
        actuatorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActuators.setAdapter(actuatorArrayAdapter);

        spinnerActuators.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actuator = (Actuator) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        EditText deviceNameEditText = view.findViewById(R.id.text_rule_name);
        EditText deviceDescEditText = view.findViewById(R.id.text_rule_description);
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
              /*  Rule rule = new Rule();
                rule.setName(deviceName);
                rule.setDescription(deviceDesc);
                rule.setActuator(actuator);
                rule.setSensor(sensor);
                rule.setVersionTimestamp(System.currentTimeMillis());*/

                dbManager.addRule(deviceName,deviceDesc,sensor.getId(),actuator.getId(),currentUserId,System.currentTimeMillis());

                // update device display
                new PopulateRules(this, ruleRecyclerView).execute(currentUserId);
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "Please fill in rule data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getSelectedSensor(View v)
    {
        Sensor s = (Sensor) spinnerSensors.getSelectedItem();
    }
}
