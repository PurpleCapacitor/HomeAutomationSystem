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
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.has.DeviceInfoActivity;
import com.has.R;
import com.has.SensorActivity;
import com.has.data.DatabaseManager;
import com.has.model.Sensor;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private List<Sensor> sensorList;
    private Context context;
    private DatabaseManager dbManager;

    public SensorAdapter(List<Sensor> sensorList, Context context) {
        this.sensorList = sensorList;
        this.context = context;
        this.dbManager = new DatabaseManager(context.getApplicationContext());
    }

    public static class SensorViewHolder extends ViewHolder {

        public TextView heading;
        public TextView description;
        public ImageButton popupMenu;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.text_name_list);
            description = itemView.findViewById(R.id.text_description_list);
            popupMenu = itemView.findViewById(R.id.button_popup_menu);
        }
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new SensorAdapter.SensorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SensorViewHolder holder, int position) {
        final Sensor sensor = sensorList.get(position);
        holder.heading.setText(sensor.getReference());
        holder.description.setText(sensor.getDescription());
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
                               additionalInfo(sensor);
                                break;
                            case R.id.edit:
                                openEditSensorDialog(sensor, position);
                                break;
                            case R.id.delete:
                                dbManager.deleteSensor(sensor.getId());
                                sensorList.remove(position);
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

    private void openEditSensorDialog(Sensor sensor, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_sensor_actuator, null);
        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.edit_sensor);
        EditText deviceNameEditText = view.findViewById(R.id.text_device_name);
        deviceNameEditText.setText(sensor.getReference());
        EditText deviceDescEditText = view.findViewById(R.id.text_device_description);
        deviceDescEditText.setText(sensor.getDescription());
        EditText deviceValueEditText = view.findViewById(R.id.text_device_value);
        deviceValueEditText.setText(sensor.getValue());
        builder.setView(view)
                .setNegativeButton(context.getResources().getString(R.string.button_cancel), (dialogInterface, i) -> {})
                .setPositiveButton(context.getResources().getString(R.string.button_edit), null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String deviceName = deviceNameEditText.getText().toString();
            String deviceDesc = deviceDescEditText.getText().toString();
            String value = deviceValueEditText.getText().toString();
            if (deviceName.length() != 0 && deviceDesc.length() != 0 && value.length() != 0) {
                dbManager.updateSensor(sensor.getId(), deviceName, deviceDesc, sensor.getDevice().getId(), value, System.currentTimeMillis());

                //update sensor data
                Sensor updatedSensor = sensorList.get(position);
                updatedSensor.setReference(deviceName);
                updatedSensor.setDescription(deviceDesc);
                updatedSensor.setValue(value);
                notifyItemChanged(position);
                dialog.dismiss();
            } else {
                Toast.makeText(context.getApplicationContext(), R.string.fill_sensor_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void additionalInfo(Sensor sensor) {
        Intent intent = new Intent(context, SensorActivity.class);
        intent.putExtra("sensorName", sensor.getReference());
        intent.putExtra("sensorDesc", sensor.getDescription());
        intent.putExtra("sensorValue", sensor.getValue());

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }


}
