package com.has.async;

import com.has.data.GetData;
import com.has.data.RetrofitClient;
import com.has.model.User;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.util.concurrent.TimeUnit.SECONDS;

public class GenerateDataSensor {


    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void generate() {
        final Runnable beeper = new Runnable() {
            public void run() {

                GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
                apiService.generate().enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200) {

                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });

            }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
        /*scheduler.schedule(new Runnable() {
            public void run() { beeperHandle.cancel(true); }
        }, 60 * 60, SECONDS);*/
    }
}
