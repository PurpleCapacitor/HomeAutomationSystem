package com.has.adapters;

import android.content.Context;
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

import com.has.R;
import com.has.model.Action;

import java.util.List;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> {

    private List<Action> actionList;
    private Context context;

    public ActionAdapter(List<Action> actionList, Context context) {
        this.actionList = actionList;
        this.context = context;
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

    @Override
    public int getItemCount() {
        return actionList.size();
    }


}
