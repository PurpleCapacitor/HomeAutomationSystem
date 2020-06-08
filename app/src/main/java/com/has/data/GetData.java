package com.has.data;

import com.has.model.Action;
import com.has.model.Actuator;
import com.has.model.Device;
import com.has.model.Sensor;
import com.has.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GetData {

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String id);

    //DEVICES

    @GET("devices")
    Call<List<Device>> getDevices();

    @GET("devices/users/{id}")
    Call<List<Device>> getDevicesByUserId(@Path("id") Long id);

    @GET("devices/{id}")
    Call<Device> getDevice(@Path("id") String id);

    @DELETE("devices/{id}")
    Call<Void> deleteDevice(@Path("id") Long id);

    @POST("devices")
    @FormUrlEncoded
    Call<Long> createDevice(@Field("name") String name,
                            @Field("description") String description,
                            @Field("userId") Long userId,
                            @Field("versionTimestamp") Long versionTimestamp);

    @PUT("devices/{id}")
    @FormUrlEncoded
    Call<Void> updateDevice(@Path("id") Long id,
                            @Field("name") String name,
                            @Field("description") String description,
                            @Field("versionTimestamp") Long versionTimestamp,
                            @Field("userId") Long userId);

    //ACTUATORS

    @GET("actuators/devices/{id}")
    Call<List<Actuator>> getActuatorsByDeviceId(@Path("id") Long id);

    @GET("actuators")
    Call<Device> getActuators();

    @GET("actuators/{id}")
    Call<Actuator> getActuator(@Path("id") String id);

    @DELETE("actuators/{id}")
    Call<Void> deleteActuator(@Path("id") Long id);

    @POST("actuators")
    @FormUrlEncoded
    Call<Long> createActuator(@Field("reference") String reference,
                              @Field("description") String description,
                              @Field("value") String value,
                              @Field("deviceId") Long deviceId,
                              @Field("versionTimestamp") Long versionTimestamp);

    @PUT("actuators/{id}")
    @FormUrlEncoded
    Call<Void> updateActuator(@Path("id") Long id,
                              @Field("reference") String reference,
                              @Field("description") String description,
                              @Field("value") String value,
                              @Field("deviceId") Long deviceId,
                              @Field("versionTimestamp") Long versionTimestamp);
    //SENSORS
    @GET("sensors/devices/{id}")
    Call<List<Sensor>> getSensorsByDeviceId(@Path("id") Long id);

    @GET("sensors")
    Call<Sensor> getSensors();

    @GET("sensors/{id}")
    Call<Sensor> getSensor(@Path("id") String id);

    @DELETE("sensors/{id}")
    Call<Void> deleteSensor(@Path("id") Long id);

    @POST("sensors")
    @FormUrlEncoded
    Call<Long> createSensor(@Field("reference") String reference,
                            @Field("description") String description,
                            @Field("value") String value,
                            @Field("timestamp") Long timestamp,
                            @Field("deviceId") Long deviceId);

    @PUT("sensors/{id}")
    @FormUrlEncoded
    Call<Void> updateSensor(@Path("id") Long id,
                            @Field("reference") String reference,
                            @Field("description") String description,
                            @Field("value") String value,
                            @Field("timestamp") Long timestamp,
                            @Field("deviceId") Long deviceId);


    //USER
    @POST("user/login")
    @FormUrlEncoded
    Call<Long> login(@Field("email") String email, @Field("password") String password);

    @POST("user/register")
    @FormUrlEncoded
    Call<Void> register(@Field("email") String email,
                        @Field("password") String password,
                        @Field("firstName") String firstName,
                        @Field("lastName") String lastName);

}
