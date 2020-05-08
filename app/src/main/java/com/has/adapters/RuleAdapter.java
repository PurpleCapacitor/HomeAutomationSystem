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
import com.has.model.Rule;

import java.util.List;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.RuleViewHolder> {

    private List<Rule> ruleList;
    private Context context;

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
}
