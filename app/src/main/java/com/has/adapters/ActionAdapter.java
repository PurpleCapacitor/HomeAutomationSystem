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
import com.has.model.Action;
import com.has.model.Actuator;

import java.util.List;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> {

    private List<Action> actionList;
    private Context context;
    private DatabaseManager dbManager;

    public ActionAdapter(List<Action> actionList, Context context) {
        this.actionList = actionList;
        this.context = context;
        this.dbManager = new DatabaseManager(context.getApplicationContext());
    }

    public static class ActionViewHolder extends RecyclerView.ViewHolder {

        public TextView heading;
        public TextView description;
        public ImageButton popupMenu;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.text_name_list);
            description = itemView.findViewById(R.id.text_description_list);
            popupMenu = itemView.findViewById(R.id.button_popup_menu);
        }
    }

    @NonNull
    @Override
    public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new ActionAdapter.ActionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ActionViewHolder holder, int position) {
        final Action action = actionList.get(position);
        holder.heading.setText(action.getName());
        holder.description.setText(action.getDescription());
        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.popupMenu);
                popupMenu.getMenu().add(Menu.NONE, R.id.edit, 1, "Edit");
                popupMenu.getMenu().add(Menu.NONE, R.id.delete, 2, "Delete");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                openEditActionDialog(action, position);
                                break;
                            case R.id.delete:
                                dbManager.deleteAction(action.getId());
                                actionList.remove(position);
                                notifyItemRemoved(position);
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

    private void openEditActionDialog(Action action, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_action, null);
        TextView title = view.findViewById(R.id.text_device_title);
        title.setText(R.string.edit_action);
        EditText deviceNameEditText = view.findViewById(R.id.text_device_name);
        deviceNameEditText.setText(action.getName());
        EditText deviceDescEditText = view.findViewById(R.id.text_device_description);
        deviceDescEditText.setText(action.getDescription());
        EditText deviceValueEditText = view.findViewById(R.id.text_device_value);
        deviceValueEditText.setText(action.getAction());
        builder.setView(view)
                .setNegativeButton(R.string.button_cancel, (dialogInterface, i) -> {})
                .setPositiveButton(R.string.button_edit, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String deviceName = deviceNameEditText.getText().toString();
            String deviceDesc = deviceDescEditText.getText().toString();
            String devAction = deviceValueEditText.getText().toString();
            if (deviceName.length() != 0 && deviceDesc.length() != 0 && devAction.length() != 0) {
                dbManager.updateAction(action.getId(), deviceName, deviceDesc, devAction, action.getActuator().getId());

                //update action
                Action updatedAction = actionList.get(position);
                updatedAction.setName(deviceName);
                updatedAction.setDescription(deviceDesc);
                updatedAction.setAction(devAction);
                notifyItemChanged(position);
                dialog.dismiss();
            } else {
                Toast.makeText(context.getApplicationContext(), "Please fill in all action data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return actionList.size();
    }


}
