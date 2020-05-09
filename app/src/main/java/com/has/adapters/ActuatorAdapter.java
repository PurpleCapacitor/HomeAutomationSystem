package com.has.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.has.ActuatorActivity;
import com.has.R;
import com.has.model.Actuator;

import java.util.List;

public class ActuatorAdapter extends RecyclerView.Adapter<ActuatorAdapter.ActuatorViewHolder> {

    private List<Actuator> actuatorList;
    private Context context;

    public ActuatorAdapter(List<Actuator> actuatorList, Context context) {
        this.actuatorList = actuatorList;
        this.context = context;
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
                popupMenu.getMenu().add(Menu.NONE, R.id.see_more, 1, "See more");
                popupMenu.getMenu().add(Menu.NONE, R.id.edit, 2, "Edit");
                popupMenu.getMenu().add(Menu.NONE, R.id.delete, 3, "Delete");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.see_more:
                                additionalInfo();
                                break;
                            case R.id.edit:
                                Toast.makeText(context, "Edit", Toast.LENGTH_LONG).show();
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

    private void additionalInfo() {
        Intent intent = new Intent(context, ActuatorActivity.class);
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
