package com.has.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.has.DeviceInfoActivity;
import com.has.R;
import com.has.data.DatabaseManager;
import com.has.model.Device;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> deviceList;
    private Context context;
    private DatabaseManager databaseManager;

    public DeviceAdapter(List<Device> deviceList, Context context) {
        this.deviceList = deviceList;
        this.context = context;
    }

    //connecting views for each data items
    public static class DeviceViewHolder extends RecyclerView.ViewHolder {

        private TextView heading;
        private TextView description;
        private ImageButton popupMenu;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.text_name_list);
            description = itemView.findViewById(R.id.text_description_list);
            popupMenu = itemView.findViewById(R.id.button_popup_menu);
        }
    }


    @NonNull
    @Override
    public DeviceAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new DeviceAdapter.DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceAdapter.DeviceViewHolder holder, int position) {
        final Device device = deviceList.get(position);
        holder.heading.setText(device.getName());
        holder.description.setText(device.getDescription());
        holder.popupMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.popupMenu);
            popupMenu.getMenu().add(Menu.NONE, R.id.see_more, 1, "See more");
            popupMenu.getMenu().add(Menu.NONE, R.id.share, 2, "Share");
            popupMenu.getMenu().add(Menu.NONE, R.id.delete, 3, "Delete");
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.see_more:
                        detailedView(device);
                        break;
                    case R.id.share:
                        Toast.makeText(context, "share", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.delete:
                        databaseManager.deleteDevice(device.getId());
                        Toast.makeText(context, "delete", Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    private void detailedView(Device device) {
        Intent intent = new Intent(context, DeviceInfoActivity.class);
        intent.putExtra("device", device);
        context.startActivity(intent);
    }


}
