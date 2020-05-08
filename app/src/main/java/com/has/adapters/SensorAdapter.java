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
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.has.R;
import com.has.model.Sensor;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private List<Sensor> sensorList;
    private Context context;

    public SensorAdapter(List<Sensor> sensorList, Context context) {
        this.sensorList = sensorList;
        this.context = context;
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
                popupMenu.inflate(R.menu.popup_options_menu); //TODO ne treba da bude share ukljucen,
                // pravi drugi popup ili vidi dal moze nesto da se iskljuci
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
        return sensorList.size();
    }


}
