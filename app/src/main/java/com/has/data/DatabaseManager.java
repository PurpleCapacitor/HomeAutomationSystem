package com.has.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;

import com.google.gson.Gson;
import com.has.async.ActionSync;
import com.has.async.ActuatorSync;
import com.has.async.DeviceSync;
import com.has.async.RuleSync;
import com.has.async.SensorSync;
import com.has.model.Action;
import com.has.model.Actuator;
import com.has.model.Device;
import com.has.model.Rule;
import com.has.model.Sensor;
import com.has.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public DatabaseManager(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // devices

    public void addDevice(String name, String description, Long loggedUserId, Long versionTimestamp) {
        //TODO try catch da bude ubudce da vidis dal ima konekcije ako nema da vratis gresku da ne pukne app
        String[] params = { name, description, loggedUserId.toString(), versionTimestamp.toString() };
        AsyncTask<String, Void, Long> id = new DeviceSync().execute(params);
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.CN_ID, id.get());
            values.put(DatabaseHelper.CN_NAME, name);
            values.put(DatabaseHelper.CN_DESCRIPTION, description);
            values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, versionTimestamp);
            db.insert(DatabaseHelper.TABLE_DEVICES, null, values);
            connectDevicesAndUsers(id.get(), loggedUserId);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addDeviceAndroid(Device device, Long loggedUserId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, device.getId());
        values.put(DatabaseHelper.CN_NAME, device.getName());
        values.put(DatabaseHelper.CN_DESCRIPTION, device.getDescription());
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, device.getVersionTimestamp());
        db.insert(DatabaseHelper.TABLE_DEVICES, null, values); //row id
        connectDevicesAndUsers(device.getId(), loggedUserId);
    }

    public List<Device> getAllDevices() {
        List<Device> devices = new ArrayList<>();
        String query = "select * from " + DatabaseHelper.TABLE_DEVICES;
        Log.d("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Device device = new Device();
                device.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CN_ID)));
                device.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CN_NAME)));
                device.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
                device.setVersionTimestamp(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CN_VERSION_TIMESTAMP)));
                devices.add(device);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return devices;
    }

    public List<Device> getDevicesByUserId(Long id) {
        List<Device> devices = new ArrayList<>();
        String query = "select * from " + DatabaseHelper.TABLE_USERS_DEVICES + " where " +
                DatabaseHelper.CN_USER_ID + " = " + id;
        Log.d("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Device d = getDevice(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
                devices.add(d);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return devices;
    }


    public Device getDevice(Long id) {
        Device device = new Device();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_DEVICES + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
        } else
            c.moveToFirst();

        device.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        device.setName(c.getString(c.getColumnIndex(DatabaseHelper.CN_NAME)));
        device.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        device.setVersionTimestamp(c.getLong(c.getColumnIndex(DatabaseHelper.CN_VERSION_TIMESTAMP)));
        c.close();
        return device;
    }

    public Device getDeviceByName(String name) {
        Device device = new Device();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_DEVICES + " where " + DatabaseHelper.CN_NAME + " = '" + name + "'";
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
        } else
            c.moveToFirst();

        device.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        device.setName(c.getString(c.getColumnIndex(DatabaseHelper.CN_NAME)));
        device.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        device.setVersionTimestamp(c.getLong(c.getColumnIndex(DatabaseHelper.CN_VERSION_TIMESTAMP)));
        c.close();
        return device;
    }

    public void updateDevice(Long id, String name, String description, Long versionTimestamp, Long userId) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.updateDevice(id, name, description, versionTimestamp, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.CN_NAME, name);
                values.put(DatabaseHelper.CN_DESCRIPTION, description);
                values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, versionTimestamp);
                db.update(DatabaseHelper.TABLE_DEVICES, values, DatabaseHelper.CN_ID + " = " + id, null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    public int updateDeviceAndroid(Device device) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, device.getId());
        values.put(DatabaseHelper.CN_NAME, device.getName());
        values.put(DatabaseHelper.CN_DESCRIPTION, device.getDescription());
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, device.getVersionTimestamp());
        return db.update(DatabaseHelper.TABLE_DEVICES, values, DatabaseHelper.CN_ID + " = " + device.getId(), null);
    }

    public void deleteDevice(Long id) {
        //TODO kad se brise, brisi sve vezane aktuatore i senzore pa onda u tim aktuatorima brises njihove akcije i brises pravila
        //vezana za te aktuatore i za senzore
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.deleteDevice(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db = dbHelper.getWritableDatabase();
                String selection = DatabaseHelper.CN_ID + " LIKE ?";
                String[] args = {String.valueOf(id)};
                db.delete(DatabaseHelper.TABLE_DEVICES, selection, args);
                String deleteQuery = "delete from " + DatabaseHelper.TABLE_USERS_DEVICES + " where " +
                        DatabaseHelper.CN_DEVICE_ID + " = " + id;
                db.execSQL(deleteQuery);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    // actuators

    public void addActuator(String reference, String description, Long deviceId, String value,
                            Long versionTimestamp) {
        String[] params = { reference, description, value, deviceId.toString(), versionTimestamp.toString() };
        AsyncTask<String, Void, Long> id = new ActuatorSync().execute(params);
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.CN_ID, id.get());
            values.put(DatabaseHelper.CN_REFERENCE, reference);
            values.put(DatabaseHelper.CN_DESCRIPTION, description);
            values.put(DatabaseHelper.CN_DEVICE_ID, deviceId);
            values.put(DatabaseHelper.CN_VALUE, value);
            values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, versionTimestamp);
            db.insert(DatabaseHelper.TABLE_ACTUATORS, null, values);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addActuatorAndroid(Actuator actuator) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, actuator.getId());
        values.put(DatabaseHelper.CN_REFERENCE, actuator.getReference());
        values.put(DatabaseHelper.CN_DESCRIPTION, actuator.getDescription());
        values.put(DatabaseHelper.CN_DEVICE_ID, actuator.getDevice().getId());
        values.put(DatabaseHelper.CN_VALUE, actuator.getValue());
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, actuator.getVersionTimestamp());
        db.insert(DatabaseHelper.TABLE_ACTUATORS, null, values);
    }

    public Actuator getActuator(Long id) {
        Actuator actuator = new Actuator();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_ACTUATORS + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
        } else
            c.moveToFirst();

        actuator.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        actuator.setReference(c.getString(c.getColumnIndex(DatabaseHelper.CN_REFERENCE)));
        actuator.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        actuator.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
        Device d = getDevice(c.getLong(c.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
        actuator.setDevice(d);

        return actuator;
    }

    public List<Actuator> getAllActuators() {
        List<Actuator> actuators = new ArrayList<>();
        String query = "select * from " + DatabaseHelper.TABLE_ACTUATORS;
        Log.i("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Actuator actuator = new Actuator();
                actuator.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
                actuator.setReference(c.getString(c.getColumnIndex(DatabaseHelper.CN_REFERENCE)));
                actuator.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
                actuator.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
                Device d = getDevice(c.getLong(c.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
                actuator.setDevice(d);
                actuators.add(actuator);
            } while (c.moveToNext());
        }
        c.close();
        return actuators;
    }

    public List<Actuator> getActuatorsByDeviceId(Long id) {
        List<Actuator> actuators = new ArrayList<>();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_ACTUATORS + " where " +
                DatabaseHelper.CN_DEVICE_ID + " = " + id;
        Log.i("DATABASE QUERY", selectQuery);

        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Actuator actuator = new Actuator();
                actuator.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
                actuator.setReference(c.getString(c.getColumnIndex(DatabaseHelper.CN_REFERENCE)));
                actuator.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
                actuator.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
                actuator.setVersionTimestamp(c.getLong(c.getColumnIndex(DatabaseHelper.CN_VERSION_TIMESTAMP)));
                Device d = getDevice(c.getLong(c.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
                actuator.setDevice(d);
                actuators.add(actuator);
            } while (c.moveToNext());
        }
        c.close();
        return actuators;
    }

    public void updateActuator(Long id, String reference, String description, Long deviceId, String value,
                               Long versionTimestamp) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.updateActuator(id, reference, description, value, deviceId, versionTimestamp).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.CN_REFERENCE, reference);
                values.put(DatabaseHelper.CN_DESCRIPTION, description);
                values.put(DatabaseHelper.CN_DEVICE_ID, deviceId);
                values.put(DatabaseHelper.CN_VALUE, value);
                values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, versionTimestamp);
                db.update(DatabaseHelper.TABLE_ACTUATORS, values, DatabaseHelper.CN_ID + " = " + id, null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    public void updateActuatorAndroid(Actuator actuator) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, actuator.getId());
        values.put(DatabaseHelper.CN_REFERENCE, actuator.getReference());
        values.put(DatabaseHelper.CN_DESCRIPTION, actuator.getDescription());
        values.put(DatabaseHelper.CN_DEVICE_ID, actuator.getDevice().getId());
        values.put(DatabaseHelper.CN_VALUE, actuator.getValue());
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, actuator.getVersionTimestamp());
        db.update(DatabaseHelper.TABLE_ACTUATORS, values, DatabaseHelper.CN_ID + " = "
                + actuator.getId(), null);
    }

    public void deleteActuator(Long id) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.deleteActuator(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db = dbHelper.getWritableDatabase();
                String selection = DatabaseHelper.CN_ID + " LIKE ?";
                String[] args = {String.valueOf(id)};
                db.delete(DatabaseHelper.TABLE_ACTUATORS, selection, args);
                //TODO i za sva pravila i akcije da brises
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    // sensors

    public void addSensor(String reference, String description, Long deviceId, String value, Long timestamp) {
        String[] params = { reference, description, value, timestamp.toString(), deviceId.toString() };
        AsyncTask<String, Void, Long> id = new SensorSync().execute(params);
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.CN_ID, id.get());
            values.put(DatabaseHelper.CN_REFERENCE, reference);
            values.put(DatabaseHelper.CN_DESCRIPTION, description);
            values.put(DatabaseHelper.CN_DEVICE_ID, deviceId);
            values.put(DatabaseHelper.CN_VALUE, value);
            values.put(DatabaseHelper.CN_TIMESTAMP, timestamp);
            db.insert(DatabaseHelper.TABLE_SENSORS, null, values);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addSensorAndroid(Sensor sensor) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, sensor.getId());
        values.put(DatabaseHelper.CN_REFERENCE, sensor.getReference());
        values.put(DatabaseHelper.CN_DESCRIPTION, sensor.getDescription());
        values.put(DatabaseHelper.CN_DEVICE_ID, sensor.getDevice().getId());
        values.put(DatabaseHelper.CN_VALUE, sensor.getValue());
        values.put(DatabaseHelper.CN_TIMESTAMP, sensor.getTimestamp());
        db.insert(DatabaseHelper.TABLE_SENSORS, null, values);

    }

    public Sensor getSensor(Long id) {
        Sensor sensor = new Sensor();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_SENSORS + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
        } else
            c.moveToFirst();

        sensor.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        sensor.setReference(c.getString(c.getColumnIndex(DatabaseHelper.CN_REFERENCE)));
        sensor.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        sensor.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
        sensor.setTimestamp(c.getLong(c.getColumnIndex((DatabaseHelper.CN_TIMESTAMP))));
        Device d = getDevice(c.getLong(c.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
        sensor.setDevice(d);
        c.close();
        return sensor;
    }

    public List<Sensor> getAllSensors() {
        List<Sensor> sensors = new ArrayList<>();
        String query = "select * from " + DatabaseHelper.TABLE_SENSORS;
        Log.i("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Sensor sensor = new Sensor();
                sensor.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
                sensor.setReference(c.getString(c.getColumnIndex(DatabaseHelper.CN_REFERENCE)));
                sensor.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
                sensor.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
                sensor.setTimestamp(c.getLong(c.getColumnIndex((DatabaseHelper.CN_TIMESTAMP))));
                Device d = getDevice(c.getLong(c.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
                sensor.setDevice(d);
                sensors.add(sensor);
            } while (c.moveToNext());
        }
        c.close();
        return sensors;
    }

    public List<Sensor> getSensorsByDeviceId(Long id) {
        List<Sensor> sensors = new ArrayList<>();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_SENSORS + " where " +
                DatabaseHelper.CN_DEVICE_ID + " = " + id;
        Log.i("DATABASE QUERY", selectQuery);

        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Sensor sensor = new Sensor();
                sensor.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
                sensor.setReference(c.getString(c.getColumnIndex(DatabaseHelper.CN_REFERENCE)));
                sensor.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
                sensor.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
                sensor.setTimestamp(c.getLong(c.getColumnIndex((DatabaseHelper.CN_TIMESTAMP))));
                Device d = getDevice(c.getLong(c.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
                sensor.setDevice(d);
                sensors.add(sensor);
            } while (c.moveToNext());
        }
        c.close();
        return sensors;
    }

    public void updateSensor(Long id, String reference, String description, Long deviceId, String value, Long timestamp) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.updateSensor(id, reference, description, value, timestamp, deviceId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.CN_REFERENCE, reference);
                values.put(DatabaseHelper.CN_DESCRIPTION, description);
                values.put(DatabaseHelper.CN_DEVICE_ID, deviceId);
                values.put(DatabaseHelper.CN_VALUE, value);
                values.put(DatabaseHelper.CN_TIMESTAMP, timestamp);
                db.update(DatabaseHelper.TABLE_SENSORS, values, DatabaseHelper.CN_ID + " = " + id, null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void updateSensorAndroid(Sensor sensor) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, sensor.getId());
        values.put(DatabaseHelper.CN_REFERENCE, sensor.getReference());
        values.put(DatabaseHelper.CN_DESCRIPTION, sensor.getDescription());
        values.put(DatabaseHelper.CN_DEVICE_ID, sensor.getDevice().getId());
        values.put(DatabaseHelper.CN_VALUE, sensor.getValue());
        values.put(DatabaseHelper.CN_TIMESTAMP, sensor.getTimestamp());
        db.update(DatabaseHelper.TABLE_SENSORS, values, DatabaseHelper.CN_ID + " = " + sensor.getId(), null);
    }

    public void deleteSensor(Long id) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.deleteSensor(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db = dbHelper.getWritableDatabase();
                String selection = DatabaseHelper.CN_ID + " LIKE ?";
                String[] args = {String.valueOf(id)};
                db.delete(DatabaseHelper.TABLE_SENSORS,selection,args);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    // actions

    public void addAction(String name, String description, String action, Long actuatorId, Long versionTimestamp) {
        String[] params = { name, description, action, actuatorId.toString(), versionTimestamp.toString() };
        AsyncTask<String, Void, Long> id = new ActionSync().execute(params);
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.CN_ID, id.get());
            values.put(DatabaseHelper.CN_NAME, name);
            values.put(DatabaseHelper.CN_DESCRIPTION, description);
            values.put(DatabaseHelper.CN_ACTION, action);
            values.put(DatabaseHelper.CN_ACTUATOR_ID, actuatorId);
            values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, versionTimestamp);
            db.insert(DatabaseHelper.TABLE_ACTIONS, null, values);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void addActionAndroid(Action action) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, action.getId());
        values.put(DatabaseHelper.CN_NAME, action.getName());
        values.put(DatabaseHelper.CN_DESCRIPTION, action.getDescription());
        values.put(DatabaseHelper.CN_ACTION, action.getAction());
        values.put(DatabaseHelper.CN_ACTUATOR_ID, action.getActuator().getId());
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, action.getVersionTimestamp());
        db.insert(DatabaseHelper.TABLE_ACTIONS, null, values);
    }

    public Action getAction(Long id) {
        Action action = new Action();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_ACTIONS + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
        } else
            c.moveToFirst();

        action.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        action.setName(c.getString(c.getColumnIndex(DatabaseHelper.CN_NAME)));
        action.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        action.setAction(c.getString(c.getColumnIndex(DatabaseHelper.CN_ACTION)));
        Actuator a = getActuator(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ACTUATOR_ID)));
        action.setActuator(a);
        c.close();
        return action;
    }

    public List<Action> getAllActions() {
        List<Action> actions = new ArrayList<>();
        String query = "select * from " + DatabaseHelper.TABLE_ACTUATORS;
        Log.i("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Action action = new Action();
                action.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
                action.setName(c.getString(c.getColumnIndex(DatabaseHelper.CN_NAME)));
                action.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
                action.setAction(c.getString(c.getColumnIndex(DatabaseHelper.CN_ACTION)));
                Actuator a = getActuator(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ACTUATOR_ID)));
                action.setActuator(a);
                actions.add(action);
            } while (c.moveToNext());
        }
        c.close();
        return actions;
    }

    public List<Action> getActionsByActuator(Long id) {
        List<Action> actions = new ArrayList<>();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_ACTIONS + " where " +
                DatabaseHelper.CN_ACTUATOR_ID + " = " + id;
        Log.i("DATABASE QUERY", selectQuery);

        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Action action = new Action();
                action.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
                action.setName(c.getString(c.getColumnIndex(DatabaseHelper.CN_NAME)));
                action.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
                action.setAction(c.getString(c.getColumnIndex(DatabaseHelper.CN_ACTION)));
                action.setVersionTimestamp(c.getLong(c.getColumnIndex(DatabaseHelper.CN_VERSION_TIMESTAMP)));
                Actuator a = getActuator(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ACTUATOR_ID)));
                action.setActuator(a);
                actions.add(action);
            } while (c.moveToNext());
        }
        c.close();
        return actions;
    }

    public void updateAction(Long id, String name, String description, String action, Long actuatorId,
                            Long versionTimestamp) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.updateAction(id, name, description, action, actuatorId, versionTimestamp).enqueue(
                new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.CN_NAME, name);
                        values.put(DatabaseHelper.CN_DESCRIPTION, description);
                        values.put(DatabaseHelper.CN_ACTION, action);
                        values.put(DatabaseHelper.CN_ACTUATOR_ID, actuatorId);
                        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, versionTimestamp);
                        db.update(DatabaseHelper.TABLE_ACTIONS, values, DatabaseHelper.CN_ID + " = " + id, null);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                }
        );
    }

    public void updateActionAndroid(Action action) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, action.getId());
        values.put(DatabaseHelper.CN_NAME, action.getName());
        values.put(DatabaseHelper.CN_DESCRIPTION, action.getDescription());
        values.put(DatabaseHelper.CN_ACTION, action.getAction());
        values.put(DatabaseHelper.CN_ACTUATOR_ID, action.getActuator().getId());
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, action.getVersionTimestamp());
        db.update(DatabaseHelper.TABLE_ACTIONS, values, DatabaseHelper.CN_ID + " = " + action.getId(), null);
    }

    public void deleteAction(Long id) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.deleteAction(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db = dbHelper.getWritableDatabase();
                String selection = DatabaseHelper.CN_ID + " LIKE ?";
                String[] args = {String.valueOf(id)};
                db.delete(DatabaseHelper.TABLE_ACTIONS, selection, args);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    // users and shared devices

    public User getUser(Long id) {
        User user = new User();

        String selectQuery = "select * from " + DatabaseHelper.TABLE_USERS + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
        } else
            c.moveToFirst();

        user.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        user.setEmail(c.getString(c.getColumnIndex(DatabaseHelper.CN_EMAIL)));
        user.setFirstName(c.getString(c.getColumnIndex(DatabaseHelper.CN_FIRST_NAME)));
        user.setLastName(c.getString(c.getColumnIndex(DatabaseHelper.CN_LAST_NAME)));
        user.setPassword(c.getString(c.getColumnIndex(DatabaseHelper.CN_PASSWORD)));
        List<Device> devices = getSharedDevicesByUserId(user.getId());
        user.setSharedDevices(devices);

        c.close();
        return user;
    }

    public List<Device> getSharedDevicesByUserId(Long id) {
        List<Device> devices = new ArrayList<>();
        String query = "select * from " + DatabaseHelper.TABLE_USERS_DEVICES + " where " +
                DatabaseHelper.CN_USER_ID + " = " + id + " and " + DatabaseHelper.CN_SHARED + " = 1";
        Log.d("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Device d = getDevice(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
                devices.add(d);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return devices;
    }

    public List<Device> getSharedDevicesByUserEmail(String email) {
        List<Device> devices = new ArrayList<>();
        String query = "select * from " + DatabaseHelper.TABLE_USERS_DEVICES + " where " +
                DatabaseHelper.CN_EMAIL + " = '" + email + "' and " + DatabaseHelper.CN_SHARED + " = 1";
        Log.d("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Device d = getDevice(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
                devices.add(d);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return devices;
    }

    public long connectDevicesAndUsers(Long deviceId, Long userId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_USER_ID, userId);
        values.put(DatabaseHelper.CN_DEVICE_ID, deviceId);
        values.put(DatabaseHelper.CN_SHARED, 0);
        return db.insert(DatabaseHelper.TABLE_USERS_DEVICES, null, values);
    }


    public User getUserByEmail(String email) {
        User user = new User();

        String selectQuery = "select * from " + DatabaseHelper.TABLE_USERS + " where " + DatabaseHelper.CN_EMAIL + " = '" +
                email + "'";
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
        } else
            c.moveToFirst();

        user.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        user.setEmail(c.getString(c.getColumnIndex(DatabaseHelper.CN_EMAIL)));
        user.setFirstName(c.getString(c.getColumnIndex(DatabaseHelper.CN_FIRST_NAME)));
        user.setLastName(c.getString(c.getColumnIndex(DatabaseHelper.CN_LAST_NAME)));
        user.setPassword(c.getString(c.getColumnIndex(DatabaseHelper.CN_PASSWORD)));
        List<Device> devices = getSharedDevicesByUserId(user.getId());
        user.setSharedDevices(devices);

        c.close();
        return user;
    }

    public int updateUser(Long id, String email, String password, String firstName, String lastName) { // samo za user settings
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_EMAIL, email);
        values.put(DatabaseHelper.CN_PASSWORD, password);
        values.put(DatabaseHelper.CN_FIRST_NAME, firstName);
        values.put(DatabaseHelper.CN_LAST_NAME, lastName);
        return db.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.CN_ID + " = " + id, null);
    }

    public long addUser(String email, String password, String firstName, String lastName, Long versionTimestamp) { // samo za testiranje
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_EMAIL, email);
        values.put(DatabaseHelper.CN_PASSWORD, password);
        values.put(DatabaseHelper.CN_FIRST_NAME, firstName);
        values.put(DatabaseHelper.CN_LAST_NAME, lastName);
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, versionTimestamp);
        return db.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    public long addUserAndroid(User user) { // samo za testiranje
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, user.getId());
        values.put(DatabaseHelper.CN_EMAIL, user.getEmail());
        values.put(DatabaseHelper.CN_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.CN_FIRST_NAME, user.getFirstName());
        values.put(DatabaseHelper.CN_LAST_NAME, user.getLastName());
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, user.getVersionTimestamp());
        return db.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    // rules

    public void addRule(String name, String description, String value, String ruleRelation, String valueActuator, Long sensorId, Long actuatorId, Long userId, Long versionTimestamp) {
        String[] params = { name, description, versionTimestamp.toString(), userId.toString(), sensorId.toString(), actuatorId.toString(), value, ruleRelation, valueActuator};
        AsyncTask<String, Void, Long> id = new RuleSync().execute(params);
        try
        {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.CN_ID, id.get());
            values.put(DatabaseHelper.CN_NAME, name);
            values.put(DatabaseHelper.CN_DESCRIPTION, description);
            values.put(DatabaseHelper.CN_VALUE, value);
            values.put(DatabaseHelper.CN_RULE_RELATION, ruleRelation);
            values.put(DatabaseHelper.CN_VALUE_ACTUATOR, valueActuator);
            values.put(DatabaseHelper.CN_VERSION_TIMESTAMP,versionTimestamp);
            values.put(DatabaseHelper.CN_USER_ID, userId);
            db.insert(DatabaseHelper.TABLE_RULES, null, values);
            connectRuleAndSensor(id.get(), sensorId);
            connectRuleAndActuator(id.get(), actuatorId);
        }catch (ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    public void addRuleAndroid(Rule rule, Long loggedUserId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, rule.getId());
        values.put(DatabaseHelper.CN_NAME, rule.getName());
        values.put(DatabaseHelper.CN_DESCRIPTION, rule.getDescription());
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, rule.getVersionTimestamp());
        values.put(DatabaseHelper.CN_VALUE, rule.getValue());
        values.put(DatabaseHelper.CN_RULE_RELATION, rule.getRuleRelation());
        values.put(DatabaseHelper.CN_VALUE_ACTUATOR, rule.getValueActuator());
        values.put(DatabaseHelper.CN_USER_ID, loggedUserId);
        db.insert(DatabaseHelper.TABLE_RULES, null, values); //row id
        connectRuleAndActuator(rule.getId(), rule.getActuator().getId());
        connectRuleAndSensor(rule.getId(), rule.getSensor().getId());

    }

    public long connectRuleAndSensor(Long ruleId, Long sensorId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_RULES_ID, ruleId);
        values.put(DatabaseHelper.CN_SENSOR_ID, sensorId);
        return db.insert(DatabaseHelper.TABLE_RULES_SENSORS, null, values);
    }

    private long connectRuleAndActuator(Long ruleId, Long actuatorId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_RULES_ID, ruleId);
        values.put(DatabaseHelper.CN_ACTUATOR_ID, actuatorId);
        return db.insert(DatabaseHelper.TABLE_RULES_ACTUATORS, null, values);
    }

    public Rule getRule(Long id, Long userId) {
        Rule rule = new Rule();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_RULES + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
        } else
            c.moveToFirst();

        rule.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        rule.setName(c.getString(c.getColumnIndex(DatabaseHelper.CN_NAME)));
        rule.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        rule.setVersionTimestamp(c.getLong(c.getColumnIndex(DatabaseHelper.CN_VERSION_TIMESTAMP)));
        rule.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
        rule.setRuleRelation(c.getString(c.getColumnIndex(DatabaseHelper.CN_RULE_RELATION)));
        rule.setValueActuator(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE_ACTUATOR)));

        // User user = getUser(c.getLong(c.getColumnIndex(DatabaseHelper.CN_USER_ID)));
        User user = new User();
        user.setId(userId);
        rule.setUser(user);

        c.close();
        return rule;
    }

    public Rule getRuleByName(String name) {
        Rule rule = new Rule();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_RULES + " where " + DatabaseHelper.CN_NAME + " = '" + name + "'";
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
        } else
            c.moveToFirst();

        rule.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        rule.setName(c.getString(c.getColumnIndex(DatabaseHelper.CN_NAME)));
        rule.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        User user = getUser(c.getLong(c.getColumnIndex(DatabaseHelper.CN_USER_ID)));
        rule.setUser(user);

        c.close();
        return rule;
    }

    public List<Rule> getRulesbyUserId(Long id) {
        List<Rule> rules = new ArrayList<>();
        String query = "select * from " + DatabaseHelper.TABLE_RULES + " where " +
                DatabaseHelper.CN_USER_ID + " = " + id;
        Log.d("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Rule d = getRule(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CN_ID)), id);
                rules.add(d);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rules;
    }

    public Actuator getActuatorByRuleId(Long id) {
        Actuator actuator = new Actuator();
        String query = "select * from " + DatabaseHelper.TABLE_RULES_ACTUATORS + " where " +
                DatabaseHelper.CN_RULES_ID + " = " + id;
        Log.d("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Actuator d = getActuator(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CN_RULES_ID)));
                actuator = d;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return actuator;
    }

    public Sensor getSensorByRuleId(Long id) {
        Sensor sensor = new Sensor();
        String query = "select * from " + DatabaseHelper.TABLE_RULES_SENSORS + " where " +
                DatabaseHelper.CN_RULES_ID + " = " + id;
        Log.d("DATABASE QUERY", query);

        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Sensor d = getSensor(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.CN_RULES_ID)));
                sensor = d;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sensor;
    }

    public void updateRule(Long id, String name, String description, String value, String ruleRelation, String valueActuator, Long sensorId, Long actuatorId, Long userId, Long versionTimestamp) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.updateRule(id, name, description, value, ruleRelation, valueActuator, versionTimestamp, userId, sensorId, actuatorId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.CN_NAME, name);
                values.put(DatabaseHelper.CN_DESCRIPTION, description);
                values.put(DatabaseHelper.CN_USER_ID, userId);

                values.put(DatabaseHelper.CN_VALUE, value);
                values.put(DatabaseHelper.CN_RULE_RELATION, ruleRelation);
                values.put(DatabaseHelper.CN_VALUE_ACTUATOR, valueActuator);
                values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, versionTimestamp);
                connectRuleAndActuator(id,actuatorId);
                connectRuleAndSensor(id,sensorId);
                db.update(DatabaseHelper.TABLE_RULES, values, DatabaseHelper.CN_ID + " = " + id, null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }


  /*  public int updateRule(Long id, String name, String description) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_NAME, name);
        values.put(DatabaseHelper.CN_DESCRIPTION, description);
        return db.update(DatabaseHelper.TABLE_RULES, values, DatabaseHelper.CN_ID + " = " + id, null);
    }*/

    public int updateRuleAndroid(Rule rule) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_ID, rule.getId());
        values.put(DatabaseHelper.CN_NAME, rule.getName());
        values.put(DatabaseHelper.CN_DESCRIPTION, rule.getDescription());
        values.put(DatabaseHelper.CN_VERSION_TIMESTAMP, rule.getVersionTimestamp());
        return db.update(DatabaseHelper.TABLE_RULES, values, DatabaseHelper.CN_ID + " = " + rule.getId(), null);
    }

    public void deleteRule(Long id) {
        GetData apiService = RetrofitClient.getRetrofitInstance().create(GetData.class);
        apiService.deleteRule(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db = dbHelper.getWritableDatabase();
                String selection = DatabaseHelper.CN_ID + " LIKE ?";
                String[] args = {String.valueOf(id)};
                db.delete(DatabaseHelper.TABLE_RULES, selection, args);
                String deleteQuery = "delete from " + DatabaseHelper.TABLE_RULES_SENSORS + " where " +
                        DatabaseHelper.CN_RULES_ID + " = " + id;
                db.execSQL(deleteQuery);

                String deleteQueryu = "delete from " + DatabaseHelper.TABLE_RULES_ACTUATORS + " where " +
                        DatabaseHelper.CN_RULES_ID + " = " + id;
                db.execSQL(deleteQueryu);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }


}
