package com.has.data;

import com.has.model.Device;
import com.has.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetData {

    @GET("users/1")
    Call<User> getUser();

    @POST("devices")
    @FormUrlEncoded
    Call<Void> createDevice(@Field("id") Long id,
                            @Field("name") String name,
                            @Field("description") String description,
                            @Field("versionTimestamp") Long versionTimestamp,
                            @Field("userId") Long userId);
}
