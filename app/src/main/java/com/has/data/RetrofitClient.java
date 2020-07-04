package com.has.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    //private static final String BASE_API_URL = "http://192.168.0.12:8080";
    //private static final String BASE_API_URL = "http://172.20.10.3:8080";

    private static final String BASE_API_URL = "http://10.0.2.2:8080";

    //Prvi api je ako hocete da testirate na svom telefonu, nije ta IP addresa, kucajte na windowsu ipconfig za linux je sudo ifconfig
    //drugi je za emulator od android studija i ne treba nista da se menja

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
