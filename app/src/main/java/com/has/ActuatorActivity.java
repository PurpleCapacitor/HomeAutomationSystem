package com.has;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.has.model.Action;

import java.util.ArrayList;
import java.util.List;

public class ActuatorActivity extends AppCompatActivity {

    private List<Action> actionList = new ArrayList<>();
    private DatabaseManager dbManager;
    private RecyclerView.Adapter actionAdapter;
    private Long actuatorId;
    private RecyclerView actionRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuator);

        dbManager = new DatabaseManager(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionRecyclerView = findViewById(R.id.recycler_view_actuator_activity);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        actionRecyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        actuatorId = intent.getLongExtra("actuatorId", -1L);

        new PopulateActions(this, actionRecyclerView).execute(actuatorId);
        actionAdapter = new ActionAdapter(actionList, this);
        actionRecyclerView.setAdapter(actionAdapter);


        FloatingActionButton addButton = findViewById(R.id.floating_button_add);
        addButton.setOnClickListener(v -> openEditActionDialog());

    }

    private void openEditActionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_action, null);
        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.add_action);
        EditText deviceNameEditText = view.findViewById(R.id.text_device_name);
        EditText deviceDescEditText = view.findViewById(R.id.text_device_description);
        EditText deviceValueEditText = view.findViewById(R.id.text_device_value);
        builder.setView(view)
                .setNegativeButton(R.string.button_cancel, (dialogInterface, i) -> {})
                .setPositiveButton(R.string.button_add, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String deviceName = deviceNameEditText.getText().toString();
            String deviceDesc = deviceDescEditText.getText().toString();
            String devAction = deviceValueEditText.getText().toString();
            if (deviceName.length() != 0 && deviceDesc.length() != 0 && devAction.length() != 0) {
                dbManager.addAction(deviceName, deviceDesc, devAction, actuatorId, System.currentTimeMillis());

                //update actions
                new PopulateActions(this, actionRecyclerView).execute(actuatorId);
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "Please fill in all action data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
