package com.has.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

import com.google.gson.Gson;
import com.has.DeviceInfoActivity;
import com.has.MainActivity;
import com.has.R;
import com.has.data.DatabaseManager;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.Device;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                        detailedView(device, context);
                        break;
                    case R.id.share:
                        shareDeviceDialog(context, device.getId());
                        break;
                    case R.id.delete:
                        databaseManager = new DatabaseManager(context);
                        databaseManager.deleteDevice(device.getId());
                        deviceList.remove(position);
                        notifyItemRemoved(position);
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

    private void detailedView(Device device, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("device", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(device);
        editor.putString("device", json);
        editor.apply();

        Intent intent = new Intent(context, DeviceInfoActivity.class);
        context.startActivity(intent);
    }

    private void shareDeviceDialog(Context context, Long deviceId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_share_device, null);
        EditText userEmailText = view.findViewById(R.id.text_shared_user_email);
        builder.setView(view)
                .setNegativeButton(context.getResources().getString(R.string.button_cancel), (dialogInterface, i) -> {})
                .setPositiveButton(context.getResources().getString(R.string.share), null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String userEmail = userEmailText.getText().toString();
            if(userEmail.length() != 0) {
                GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
                apiService.shareDevice(deviceId, userEmail).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please fill in user email", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
