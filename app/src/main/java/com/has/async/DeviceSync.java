package com.has.async;

import android.os.AsyncTask;

import com.has.data.GetData;
import com.has.data.RetrofitClient;

import java.io.IOException;

import retrofit2.Response;

public class DeviceSync extends AsyncTask<String, Void, Long> {

    private Long id;

    @Override
    protected Long doInBackground(String... strings) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        try {
            Response<Long> response = apiService.createDevice(strings[0], strings[1], Long.valueOf(strings[2]),
                    Long.valueOf(strings[3])).execute();
            id = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }
}
