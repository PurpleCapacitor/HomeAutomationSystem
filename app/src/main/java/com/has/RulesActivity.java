package com.has;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.has.adapters.RuleAdapter;
import com.has.async.PopulateDevices;
import com.has.data.DatabaseManager;
import com.has.model.Rule;

import java.util.ArrayList;
import java.util.List;

public class RulesActivity extends BaseDrawerActivity {

    private List<Rule> ruleList = new ArrayList<>();
    private DatabaseManager dbManager;
    private RecyclerView.Adapter ruleAdapter;
    private Long currentUserId;
    private RecyclerView ruleRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_rules, null, false);

        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity2);
        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", 0);
        currentUserId = sharedPreferences.getLong("currentUser", 0);
        ruleRecyclerView = findViewById(R.id.recycler_view_rules_activity_rules);
        populateList();
        ruleAdapter = new RuleAdapter(ruleList, this);
        ruleRecyclerView.setAdapter(ruleAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ruleRecyclerView.setLayoutManager(layoutManager);

        FloatingActionButton addButton = findViewById(R.id.floating_button_add);
        addButton.setOnClickListener(v -> openAddNewRuleDialog());
    }

    private void populateList() {
        Rule r1 = new Rule("Rule 1", "Description 1");
        Rule r2 = new Rule("Rule 2", "Description 2");
        ruleList.add(r1);
        ruleList.add(r2);
    }

    private void openAddNewRuleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RulesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_rule, null);
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
               // dbManager.addRule(deviceName, deviceDesc, currentUserId, System.currentTimeMillis());

                // update device display
                //new PopulateDevices(this, ruleRecyclerView).execute(currentUserId);
                dialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "Please fill in rule data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
