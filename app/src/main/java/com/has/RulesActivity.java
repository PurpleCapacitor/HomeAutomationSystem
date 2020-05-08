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
import com.has.adapters.RuleAdapter;
import com.has.model.Rule;

import java.util.ArrayList;
import java.util.List;

public class RulesActivity extends AppCompatActivity {

    private List<Rule> ruleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView ruleRecyclerView = findViewById(R.id.recycleview_rules_rules_activity);
        populateList();
        RecyclerView.Adapter ruleAdapter = new RuleAdapter(ruleList, this);
        ruleRecyclerView.setAdapter(ruleAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ruleRecyclerView.setLayoutManager(layoutManager);

        FloatingActionButton addButton = findViewById(R.id.floating_button_add_device);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked add", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateList() {
        Rule r1 = new Rule("Rule 1", "Description 1");
        Rule r2 = new Rule("Rule 2", "Description 2");
        ruleList.add(r1);
        ruleList.add(r2);
    }

}
