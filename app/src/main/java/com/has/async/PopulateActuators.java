package com.has.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.has.adapters.ActuatorAdapter;
import com.has.data.DatabaseManager;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.Actuator;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Response;

public class PopulateActuators extends AsyncTask<Long, Void, Void> {

    private WeakReference<Context> contextRef;
    private Long deviceId = null;
    private DatabaseManager dbManager;
    @SuppressLint("StaticFieldLeak")
    private RecyclerView recyclerView;

    public PopulateActuators(Context context, RecyclerView recyclerView) {
        this.contextRef = new WeakReference<>(context);
        this.recyclerView = recyclerView;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        try {

            Response<List<Actuator>> response = apiService.getActuatorsByDeviceId(longs[0]).execute();
            dbManager = new DatabaseManager(contextRef.get());
            deviceId = longs[0];
            List<Actuator> appActuators = dbManager.getActuatorsByDeviceId(deviceId);
            if (appActuators.size() == response.body().size()) {
                for (Actuator actuator : appActuators) {
                    for (Actuator backend : response.body()) {
                        if (actuator.getVersionTimestamp() < backend.getVersionTimestamp()) {
                            dbManager.updateActuatorAndroid(backend);
                        }
                    }
                }
            } else {
                if (appActuators.isEmpty()) {
                    for (Actuator backend : response.body()) {
                        dbManager.addActuatorAndroid(backend);
                    }
                } else {
                    for (Actuator actuator : appActuators) {
                        for (Actuator backend : response.body()) {
                            if (!actuator.getId().equals(backend.getId())) {
                                dbManager.addActuatorAndroid(backend);
                            } else if (actuator.getVersionTimestamp() < backend.getVersionTimestamp()) {
                                dbManager.updateActuatorAndroid(backend);
                            }

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
    protected void onPostExecute(Void aVoid) {
        dbManager = new DatabaseManager(contextRef.get());
        List<Actuator> actuatorList = dbManager.getActuatorsByDeviceId(deviceId);
        RecyclerView.Adapter actuatorAdapter = new ActuatorAdapter(actuatorList, contextRef.get());
        actuatorAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(actuatorAdapter);

    }
}
