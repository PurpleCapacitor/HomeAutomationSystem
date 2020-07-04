package com.has.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.has.R;
import com.has.data.DatabaseManager;
import com.has.model.Actuator;
import com.has.model.Device;
import com.has.model.Rule;
import com.has.model.Sensor;

import java.util.ArrayList;
import java.util.List;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.RuleViewHolder> {

    private List<Rule> ruleList;
    private Context context;
    private DatabaseManager databaseManager;
    private List<Sensor> sensorList = new ArrayList<>();
    private List<Actuator> actuatorList = new ArrayList<>();
    private List<Device> deviceList = new ArrayList<>();
    private Long currentUserId;
    private Spinner spinnerSensors;
    private Spinner spinnerActuators;

    private Actuator actuator = null;
    private Sensor sensor = null;
    private Actuator actuatorPos = null;
    private Sensor sensorPos = null;
    private Spinner spinnerRelations;
    private Spinner spinnerValuesActuator;
    private String relation = null;
    private String onOff = null;


    public RuleAdapter(List<Rule> ruleList, Context context) {
        this.ruleList = ruleList;
        this.context = context;
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        SharedPreferences sharedPreferences = context.getSharedPreferences("currentUser", 0);
        currentUserId = sharedPreferences.getLong("currentUser", 0);

        //ZA SENSORE I AKTUATORE

        databaseManager = new DatabaseManager(context);
        deviceList = databaseManager.getDevicesByUserId(currentUserId);

        for (Device d : deviceList) {
            List<Sensor> sensors = databaseManager.getSensorsByDeviceId(d.getId());

            for (Sensor s : sensors) {
                sensorList.add(s);
            }

            List<Actuator> actuators = databaseManager.getActuatorsByDeviceId(d.getId());

            for (Actuator s : actuators) {
                actuatorList.add(s);
            }

        }

        return new RuleAdapter.RuleViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull final RuleViewHolder holder, int position) {
        final Rule rule = ruleList.get(position);
        holder.heading.setText(rule.getName());
        holder.description.setText(rule.getDescription());
        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.popupMenu);
                popupMenu.getMenu().add(Menu.NONE, R.id.see_more, 1, R.string.see_more);
                popupMenu.getMenu().add(Menu.NONE, R.id.edit, 2, R.string.edit);
                popupMenu.getMenu().add(Menu.NONE, R.id.delete, 3, R.string.delete);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.see_more:
                                ruleInfoDialog(rule);
                                break;
                            case R.id.edit:
                                openEditActuatorDialog(rule, position);
                                break;
                            case R.id.delete:
                                databaseManager = new DatabaseManager(context);
                                databaseManager.deleteRule(rule.getId());
                                ruleList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, R.string.deleted, Toast.LENGTH_LONG).show();
                                break;

                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ruleList.size();
    }

    public static class RuleViewHolder extends RecyclerView.ViewHolder {

        public TextView heading;
        public TextView description;
        public ImageButton popupMenu;

        public RuleViewHolder(@NonNull View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.text_name_list);
            description = itemView.findViewById(R.id.text_description_list);
            popupMenu = itemView.findViewById(R.id.button_popup_menu);
        }
    }

    private void openEditActuatorDialog(Rule rule, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_rule, null);

        spinnerSensors = (Spinner) view.findViewById(R.id.rule_spinner_sensors);
        ArrayAdapter<Sensor> sensorArrayAdapter = new ArrayAdapter<Sensor>(context, android.R.layout.simple_spinner_item, sensorList);
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
        ArrayAdapter<Actuator> actuatorArrayAdapter = new ArrayAdapter<Actuator>(context, android.R.layout.simple_spinner_item, actuatorList);
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

        List<String> relationsArray = new ArrayList<>();
        relationsArray.add(">=");
        relationsArray.add("=");
        relationsArray.add("<=");
        relationsArray.add(">");
        relationsArray.add("<");

        spinnerRelations = (Spinner) view.findViewById(R.id.rule_spinner_relations);
        ArrayAdapter<String> relations = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, relationsArray);
        relations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRelations.setAdapter(relations);

        int spinnerPosition1 = relations.getPosition(rule.getRuleRelation());
        spinnerRelations.setSelection(spinnerPosition1);


        List<String> onOfArray = new ArrayList<>();
        onOfArray.add("ON");
        onOfArray.add("OFF");

        spinnerValuesActuator = (Spinner) view.findViewById(R.id.rule_spinner_value_actuator);
        ArrayAdapter<String> onOfAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, onOfArray);
        onOfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerValuesActuator.setAdapter(onOfAdapter);

        int spinnerPosition = onOfAdapter.getPosition(rule.getValueActuator());
        spinnerValuesActuator.setSelection(spinnerPosition);

        spinnerValuesActuator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onOff = (String) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRelations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relation = (String) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.edit_rule);
        EditText deviceNameEditText = view.findViewById(R.id.text_rule_name);
        deviceNameEditText.setText(rule.getName());
        EditText deviceDescEditText = view.findViewById(R.id.text_rule_description);
        deviceDescEditText.setText(rule.getDescription());
        EditText deviceValueEditText = view.findViewById(R.id.text_sensor_value);
        deviceValueEditText.setText(rule.getValue());

       /* if (sensorPos != null) {
            int spinnerPosition = sensorArrayAdapter.getPosition(sensorPos);
            spinnerSensors.setSelection(spinnerPosition);
        }

        if (actuatorPos != null) {
            int spinnerPosition = actuatorArrayAdapter.getPosition(actuatorPos);
            spinnerActuators.setSelection(spinnerPosition);
        }
     /*   EditText deviceValueEditText = view.findViewById(R.id.text_device_value);
        deviceValueEditText.setText(actuator.getValue());*/
        builder.setView(view)
                .setNegativeButton(context.getResources().getString(R.string.button_cancel), (dialogInterface, i) -> {
                })
                .setPositiveButton(context.getResources().getString(R.string.button_edit), null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String deviceName = deviceNameEditText.getText().toString();
            String deviceDesc = deviceDescEditText.getText().toString();
            //String value = deviceValueEditText.getText().toString();
            String valueSensor = deviceValueEditText.getText().toString();

            if (deviceName.length() != 0 && deviceDesc.length() != 0 && sensor != null && actuator != null) {
                databaseManager.updateRule(rule.getId(), deviceName, deviceDesc, valueSensor, relation, onOff, sensor.getId(), actuator.getId(), currentUserId, System.currentTimeMillis());

                //update actuator
                Rule updatedActuator = ruleList.get(position);
                updatedActuator.setName(deviceName);
                updatedActuator.setDescription(deviceDesc);
                //updatedActuator.setValue(value);
                notifyItemChanged(position);
                dialog.dismiss();
            } else {
                Toast.makeText(context.getApplicationContext(), R.string.fill_actuator_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void ruleInfoDialog(Rule rule) {
        Sensor sensor = databaseManager.getSensorByRuleId(rule.getId());
        Actuator actuator = databaseManager.getActuatorByRuleId(rule.getId());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_view_rule, null);
        TextView ruleName = view.findViewById(R.id.text_rule_title);
        ruleName.setText(rule.getName());
        TextView ruleRelation = view.findViewById(R.id.text_dialog_rule_relation);
        ruleRelation.setText(context.getString(R.string.relation) + ": " + rule.getRuleRelation());
        TextView ruleSensorId = view.findViewById(R.id.text_dialog_rule_sensor_id);
        ruleSensorId.setText(context.getString(R.string.sensor_id) + ": " + sensor.getId().toString());
        TextView ruleActuatorId = view.findViewById(R.id.text_dialog_rule_actuator_id);
        ruleActuatorId.setText(context.getString(R.string.actuator_id) + ": " + actuator.getId().toString());
        TextView value = view.findViewById(R.id.text_dialog_rule_value);
        value.setText(context.getString(R.string.device_value) + ": " + rule.getValue());

        builder.setView(view)
                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
