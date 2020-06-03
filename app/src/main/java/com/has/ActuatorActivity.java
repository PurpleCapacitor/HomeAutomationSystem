package com.has;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.ActionAdapter;
import com.has.adapters.ActuatorAdapter;
import com.has.data.DatabaseManager;
import com.has.model.Action;
import com.has.model.Actuator;
import com.has.model.Device;

import java.util.ArrayList;
import java.util.List;

public class ActuatorActivity extends AppCompatActivity {

    private List<Action> actionList = new ArrayList<>();
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuator);

        dbManager = new DatabaseManager(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView actionRecyclerView = findViewById(R.id.recycler_view_actuator_activity);
        Intent intent = getIntent();
        Long id = intent.getLongExtra("actuatorId", -1L);
        dbManager.addAction("imeakcije","de","act",id);

        populate(id);

        RecyclerView.Adapter actionAdapter = new ActionAdapter(actionList, this);
        actionRecyclerView.setAdapter(actionAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        actionRecyclerView.setLayoutManager(layoutManager);

        FloatingActionButton addButton = findViewById(R.id.floating_button_add);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked add", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void populate(Long id) {

       /* Action a1 = new Action("Action 1", "Description 1");
        Action a2 = new Action("Action 2", "Description 2");
        actionList.add(a1);
        actionList.add(a2);*/

        actionList = dbManager.getActionsByActuator(id);
    }
}
