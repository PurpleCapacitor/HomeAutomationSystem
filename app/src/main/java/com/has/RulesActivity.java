package com.has;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.RuleAdapter;
import com.has.model.Rule;

import java.util.ArrayList;
import java.util.List;

public class RulesActivity extends BaseDrawerActivity {

    private List<Rule> ruleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_rules, null, false);

        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity2);

        RecyclerView ruleRecyclerView = findViewById(R.id.recycler_view_rules_activity_rules);
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
