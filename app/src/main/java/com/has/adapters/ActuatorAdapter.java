package com.has.adapters;

import android.content.Context;
import android.content.Intent;
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

import com.has.ActuatorActivity;
import com.has.DeviceInfoActivity;
import com.has.R;
import com.has.data.DatabaseManager;
import com.has.model.Actuator;
import com.has.model.Sensor;

import java.util.List;

public class ActuatorAdapter extends RecyclerView.Adapter<ActuatorAdapter.ActuatorViewHolder> {

    private List<Actuator> actuatorList;
    private Context context;
    private DatabaseManager dbManager;

    public ActuatorAdapter(List<Actuator> actuatorList, Context context) {
        this.actuatorList = actuatorList;
        this.context = context;
        this.dbManager = new DatabaseManager(context.getApplicationContext());
    }

    @NonNull
    @Override
    public ActuatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new ActuatorAdapter.ActuatorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ActuatorViewHolder holder, int position) {
        final Actuator actuator = actuatorList.get(position);
        holder.heading.setText(actuator.getReference());
        holder.description.setText(actuator.getDescription());
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
                                additionalInfo(actuator);
                                break;
                            case R.id.edit:
                                openEditActuatorDialog(actuator, position);
                                break;
                            case R.id.delete:
                                dbManager.deleteActuator(actuator.getId());
                                actuatorList.remove(position);
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

    private void openEditActuatorDialog(Actuator actuator, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_sensor_actuator, null);
        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.edit_actuator);
        EditText deviceNameEditText = view.findViewById(R.id.text_device_name);
        deviceNameEditText.setText(actuator.getReference());
        EditText deviceDescEditText = view.findViewById(R.id.text_device_description);
        deviceDescEditText.setText(actuator.getDescription());
        builder.setView(view)
                .setNegativeButton(context.getResources().getString(R.string.button_cancel), (dialogInterface, i) -> {})
                .setPositiveButton(context.getResources().getString(R.string.button_edit), null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String deviceName = deviceNameEditText.getText().toString();
            String deviceDesc = deviceDescEditText.getText().toString();
            String value = actuator.getValue();
            if (deviceName.length() != 0 && deviceDesc.length() != 0 && value.length() != 0) {
                dbManager.updateActuator(actuator.getId(), deviceName, deviceDesc, actuator.getDevice().getId(), value,
                        System.currentTimeMillis());

                //update actuator
                Actuator updatedActuator = actuatorList.get(position);
                updatedActuator.setReference(deviceName);
                updatedActuator.setDescription(deviceDesc);
                updatedActuator.setValue(value);
                notifyItemChanged(position);
                dialog.dismiss();
            } else {
                Toast.makeText(context.getApplicationContext(), R.string.fill_actuator_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void additionalInfo(Actuator actuator) {
        Intent intent = new Intent(context, ActuatorActivity.class);
        intent.putExtra("actuatorId", actuator.getId());
        intent.putExtra("actuatorName", actuator.getReference());
        intent.putExtra("actuatorDesc", actuator.getDescription());
        intent.putExtra("actuatorValue", actuator.getValue());

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return actuatorList.size();
    }

    public static class ActuatorViewHolder extends RecyclerView.ViewHolder {
        public TextView heading;
        public TextView description;
        public ImageButton popupMenu;

        public ActuatorViewHolder(@NonNull View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.text_name_list);
            description = itemView.findViewById(R.id.text_description_list);
            popupMenu = itemView.findViewById(R.id.button_popup_menu);
        }
    }
}
