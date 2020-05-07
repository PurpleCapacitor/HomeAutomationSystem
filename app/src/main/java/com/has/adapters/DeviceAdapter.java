package com.has.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.has.R;
import com.has.model.Device;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> deviceList;
    private Context context;

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

            heading = itemView.findViewById(R.id.text_device_name_list);
            description = itemView.findViewById(R.id.tex_device_description_list);
            popupMenu = itemView.findViewById(R.id.button_popup_menu);
        }
    }


    @Override
    public DeviceAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_list_items, parent, false);
        return new DeviceAdapter.DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceAdapter.DeviceViewHolder holder, int position) {
        final Device device = deviceList.get(position);
        holder.heading.setText(device.getName());
        holder.description.setText(device.getDescription());
        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.popupMenu);
                popupMenu.inflate(R.menu.popup_options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.see_more:
                                Toast.makeText(context, "See more", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.share:
                                Toast.makeText(context, "share", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.delete:
                                Toast.makeText(context, "delete", Toast.LENGTH_LONG).show();
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
        return deviceList.size();
    }


}
