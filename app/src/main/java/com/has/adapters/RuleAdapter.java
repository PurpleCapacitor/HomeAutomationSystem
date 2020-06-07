package com.has.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.has.R;
import com.has.data.DatabaseManager;
import com.has.model.Actuator;
import com.has.model.Rule;

import java.util.List;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.RuleViewHolder> {

    private List<Rule> ruleList;
    private Context context;
    private DatabaseManager databaseManager;


    public RuleAdapter(List<Rule> ruleList, Context context) {
        this.ruleList = ruleList;
        this.context = context;
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
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
                popupMenu.getMenu().add(Menu.NONE, R.id.add_sensor_and_actuator, 1, "Add sensor and actuator");
                popupMenu.getMenu().add(Menu.NONE, R.id.edit, 2, "Edit");
                popupMenu.getMenu().add(Menu.NONE, R.id.delete, 3, "Delete");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.add_sensor_and_actuator:
                                Toast.makeText(context, "Add sensor and actuator", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.edit:
                                openEditActuatorDialog(rule, position);
                                Toast.makeText(context, "Edit", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.delete:
                                databaseManager = new DatabaseManager(context);
                                databaseManager.deleteDevice(rule.getId());
                                ruleList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Delete", Toast.LENGTH_LONG).show();
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

    private void openEditActuatorDialog(Rule actuator, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_rule, null);
        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.edit_rule);
        EditText deviceNameEditText = view.findViewById(R.id.text_rule_name);
        deviceNameEditText.setText(actuator.getName());
        EditText deviceDescEditText = view.findViewById(R.id.text_rule_description);
        deviceDescEditText.setText(actuator.getDescription());
     /*   EditText deviceValueEditText = view.findViewById(R.id.text_device_value);
        deviceValueEditText.setText(actuator.getValue());*/
        builder.setView(view)
                .setNegativeButton(context.getResources().getString(R.string.button_cancel), (dialogInterface, i) -> {})
                .setPositiveButton(context.getResources().getString(R.string.button_edit), null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String deviceName = deviceNameEditText.getText().toString();
            String deviceDesc = deviceDescEditText.getText().toString();
            //String value = deviceValueEditText.getText().toString();
            if (deviceName.length() != 0 && deviceDesc.length() != 0) {
                //dbManager.updateActuator(actuator.getId(), deviceName, deviceDesc, actuator.getDevice().getId(), value);

                //update actuator
                Rule updatedActuator = ruleList.get(position);
                updatedActuator.setName(deviceName);
                updatedActuator.setDescription(deviceDesc);
                //updatedActuator.setValue(value);
                notifyItemChanged(position);
                dialog.dismiss();
            } else {
                Toast.makeText(context.getApplicationContext(), "Please fill in actuator data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
