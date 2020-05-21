package com.has.data;

import com.has.model.User;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetData {

    @GET("users/1")
    Call<User> getUser();
}
