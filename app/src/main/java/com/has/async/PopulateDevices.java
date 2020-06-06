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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

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
            Response<List<Device>> response = apiService.getDevicesByUserId(longs[0]).execute();
            dbManager = new DatabaseManager(contextRef.get());
            userId = longs[0];
            List<Device> appDevices = dbManager.getDevicesByUserId(userId);
            if(appDevices.size() == response.body().size()) {
                for (Device d : appDevices) {
                    for (Device backend : response.body()) {
                        if (d.getVersionTimestamp() < backend.getVersionTimestamp()) {
                            dbManager.updateDeviceAndroid(backend);
                        }
                    }
                }
            } else {
                for (Device d : appDevices) {
                    for (Device backend : response.body()) {
                        if (!d.getId().equals(backend.getId())) {
                            dbManager.addDeviceAndroid(backend, userId);
                        } else if (d.getVersionTimestamp() < backend.getVersionTimestamp()) {
                                dbManager.updateDeviceAndroid(backend);
                        }

                    }
                }
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
