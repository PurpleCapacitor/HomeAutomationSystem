package com.has.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.has.adapters.ActuatorAdapter;
import com.has.adapters.SensorAdapter;
import com.has.data.DatabaseManager;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.Actuator;
import com.has.model.Sensor;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Response;

public class PopulateSensors extends AsyncTask<Long, Void, Void> {

    private WeakReference<Context> contextRef;
    private Long deviceId = null;
    private DatabaseManager dbManager;
    @SuppressLint("StaticFieldLeak")
    private RecyclerView recyclerView;

    public PopulateSensors(Context context, RecyclerView recyclerView) {
        this.contextRef = new WeakReference<>(context);
        this.recyclerView = recyclerView;
    }


    @Override
    protected Void doInBackground(Long... longs) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        try {

            Response<List<Sensor>> response = apiService.getSensorsByDeviceId(longs[0]).execute();
            dbManager = new DatabaseManager(contextRef.get());
            deviceId = longs[0];
            List<Sensor> appSensors = dbManager.getSensorsByDeviceId(deviceId);
            if (appSensors.size() == response.body().size()) {
                for (Sensor sensor : appSensors) {
                    for (Sensor backend : response.body()) {
                        if (sensor.getTimestamp() < sensor.getTimestamp()) {
                            dbManager.updateSensorAndroid(backend);
                        }
                    }
                }
            } else {
                if (appSensors.isEmpty()) {
                    for (Sensor backend : response.body()) {
                        dbManager.addSensorAndroid(backend);
                    }
                } else {
                    for (Sensor sensor : appSensors) {
                        for (Sensor backend : response.body()) {
                            if (!sensor.getId().equals(backend.getId())) {
                                dbManager.addSensorAndroid(backend);
                            } else if (sensor.getTimestamp() < backend.getTimestamp()) {
                                dbManager.updateSensorAndroid(backend);
                            }

                        }
                    }
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        dbManager = new DatabaseManager(contextRef.get());
        List<Sensor> sensorList = dbManager.getSensorsByDeviceId(deviceId);
        RecyclerView.Adapter sensorAdapter = new SensorAdapter(sensorList, contextRef.get());
        sensorAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(sensorAdapter);

    }
}
