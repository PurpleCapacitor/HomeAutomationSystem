package com.has.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.has.adapters.DeviceAdapter;
import com.has.data.DatabaseManager;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.Device;
import com.has.model.User;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopulateDevices extends AsyncTask<Long, Void, Void> {

    private WeakReference<Context> contextRef;
    private ProgressDialog pd;
    private Long userId = null;
    private DatabaseManager dbManager;
    @SuppressLint("StaticFieldLeak")
    //TODO promeni da ne curi
    private RecyclerView recyclerView;

    public PopulateDevices(Context context, RecyclerView recyclerView) {
        this.contextRef = new WeakReference<>(context);
        this.recyclerView = recyclerView;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        try {
            Response<List<Device>> response = apiService.getDevices().execute();
            contextRef.get().deleteDatabase("HomeAutomation.db"); //TODO za testiranje
            dbManager = new DatabaseManager(contextRef.get());
            userId = longs[0];
            for(int i = 0; i < response.body().size(); i++) {
                Log.d("Database insert", "Current " + i);
                dbManager.addDeviceAndroid(response.body().get(i), longs[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(contextRef.get());
        pd.setTitle("Loading");
        pd.setMessage("Sync in progress");
        pd.show();
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        dbManager = new DatabaseManager(contextRef.get());
        pd.dismiss();
        List<Device> deviceList = dbManager.getDevicesByUserId(userId);
        RecyclerView.Adapter deviceAdapter = new DeviceAdapter(deviceList, contextRef.get());
        recyclerView.setAdapter(deviceAdapter);

    }



}
