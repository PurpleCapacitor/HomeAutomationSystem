package com.has;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.ActionAdapter;
import com.has.async.PopulateActions;
import com.has.data.DatabaseManager;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.listeners.ShakeDetector;
import com.has.model.Action;
import com.has.ui.ThemeHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActuatorActivity extends AppCompatActivity {

    private List<Action> actionList = new ArrayList<>();
    private DatabaseManager dbManager;
    private Long actuatorId;
    private RecyclerView actionRecyclerView;
    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private long numActions = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeHelper.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuator);


        dbManager = new DatabaseManager(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionRecyclerView = findViewById(R.id.recycler_view_actuator_activity);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        actionRecyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        actuatorId = intent.getLongExtra("actuatorId", -1L);
        String actuatorName = intent.getStringExtra("actuatorName");
        String actuatorDesc = intent.getStringExtra("actuatorValue");

        new PopulateActions(this, actionRecyclerView).execute(actuatorId);
        RecyclerView.Adapter actionAdapter = new ActionAdapter(actionList, this);
        actionRecyclerView.setAdapter(actionAdapter);
        numActions = actionAdapter.getItemCount();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                if(numActions!=0)
                    handleShakeEvent(count);
            }
        });


        FloatingActionButton addButton = findViewById(R.id.floating_button_add);
        addButton.setOnClickListener(v -> openEditActionDialog());

        TextView header = findViewById(R.id.text_component_name_info);
        header.setText(actuatorName);
        TextView description = findViewById(R.id.text_component_description_info);
        description.setText(actuatorDesc);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    public void handleShakeEvent(int count)
    {
        //TODO odradi retrofit
        Toast.makeText(getApplicationContext(), "SHAKEEEEEE", Toast.LENGTH_LONG).show();

        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.shake(actuatorId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200) {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    private void openEditActionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_action, null);
        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.add_action);
        EditText deviceNameEditText = view.findViewById(R.id.text_device_name);
        EditText deviceDescEditText = view.findViewById(R.id.text_device_description);
        builder.setView(view)
                .setNegativeButton(R.string.button_cancel, (dialogInterface, i) -> {})
                .setPositiveButton(R.string.button_add, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String actionName = deviceNameEditText.getText().toString();
            String actionDesc = deviceDescEditText.getText().toString();
            if (actionName.length() != 0 && actionDesc.length() != 0) {
                dbManager.addAction(actionName, actionDesc, "YES", actuatorId, System.currentTimeMillis());

                //update actions
                new PopulateActions(this, actionRecyclerView).execute(actuatorId);
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), R.string.fill_action_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), DeviceInfoActivity.class);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }
}
