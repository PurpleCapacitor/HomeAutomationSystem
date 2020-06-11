package com.has.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.has.adapters.DeviceAdapter;
import com.has.adapters.RuleAdapter;
import com.has.data.DatabaseManager;
import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.Device;
import com.has.model.Rule;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Response;

public class PopulateRules extends AsyncTask<Long, Void, Void> {
    private WeakReference<Context> contextRef;
    private Long userId = null;
    private DatabaseManager dbManager;
    @SuppressLint("StaticFieldLeak")
    //TODO promeni da ne curi
    private RecyclerView recyclerView;
    private static final String TAG = "MyActivity";


    public PopulateRules(Context context, RecyclerView recyclerView) {
        this.contextRef = new WeakReference<>(context);
        this.recyclerView = recyclerView;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        try {
            Response<List<Rule>> response = apiService.getRulesByUserId(longs[0]).execute();
            dbManager = new DatabaseManager(contextRef.get());
            userId = longs[0];
            List<Rule> appRules = dbManager.getRulesbyUserId(userId);
            if (appRules.size() == response.body().size()) {
                for (Rule d : appRules) {
                    for (Rule backend : response.body()) {
                        if (d.getVersionTimestamp() < backend.getVersionTimestamp()) {
                            dbManager.updateRuleAndroid(backend);
                        }
                    }
                }
            } else {
                if (appRules.isEmpty()) {
                    for (Rule backend : response.body()) {
                        dbManager.addRuleAndroid(backend, userId);
                    }
                } else {
                    for (Rule d : appRules) {
                        for (Rule backend : response.body()) {
                            if (!d.getId().equals(backend.getId())) {
                                dbManager.addRuleAndroid(backend, userId);
                            } else if (d.getVersionTimestamp() < backend.getVersionTimestamp()) {
                                dbManager.updateRuleAndroid(backend);
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
        /*pd = new ProgressDialog(contextRef.get());
        pd.dismiss();*/
        List<Rule> ruleList = dbManager.getRulesbyUserId(userId);
        Log.i(TAG, "RuleList " + ruleList);

        RecyclerView.Adapter ruleAdapter = new RuleAdapter(ruleList, contextRef.get());
        recyclerView.setAdapter(ruleAdapter);

    }
}
