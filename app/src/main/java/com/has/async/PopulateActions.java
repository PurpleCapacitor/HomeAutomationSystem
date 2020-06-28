package com.has.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.has.adapters.ActionAdapter;
import com.has.adapters.ActuatorAdapter;
import com.has.data.DatabaseManager;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.Action;
import com.has.model.Actuator;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Response;

public class PopulateActions extends AsyncTask<Long, Void, Void> {

    private WeakReference<Context> contextRef;
    private Long actuatorId = null;
    private DatabaseManager dbManager;
    @SuppressLint("StaticFieldLeak")
    private RecyclerView recyclerView;

    public PopulateActions(Context context, RecyclerView recyclerView) {
        this.contextRef = new WeakReference<>(context);
        this.recyclerView = recyclerView;
    }


    @Override
    protected Void doInBackground(Long... longs) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        try {
            Response<List<Action>> response = apiService.getActionsByActuatorId(longs[0]).execute();
            dbManager = new DatabaseManager(contextRef.get());
            actuatorId = longs[0];
            List<Action> appActions = dbManager.getActionsByActuator(actuatorId);
            if (appActions.size() == response.body().size()) {
                for (Action action : appActions) {
                    for (Action backend : response.body()) {
                        if (action.getVersionTimestamp() < backend.getVersionTimestamp()) {
                            dbManager.updateActionAndroid(backend);
                        }
                    }
                }
            } else {
                if (appActions.isEmpty()) {
                    for (Action backend : response.body()) {
                        dbManager.addActionAndroid(backend);
                    }
                } else {
                    for (Action action : appActions) {
                        for (Action backend : response.body()) {
                            if (!action.getId().equals(backend.getId())) {
                                dbManager.addActionAndroid(backend);
                            } else if (action.getVersionTimestamp() < backend.getVersionTimestamp()) {
                                dbManager.updateActionAndroid(backend);
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
        List<Action> actionList = dbManager.getActionsByActuator(actuatorId);
        RecyclerView.Adapter actionAdapter = new ActionAdapter(actionList, contextRef.get());
        actionAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(actionAdapter);

    }
}
