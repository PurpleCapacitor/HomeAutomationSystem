package com.has.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.has.model.Action;
import com.has.model.Actuator;
import com.has.model.Device;
import com.has.model.Sensor;
import com.has.model.SensorData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public DatabaseManager(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // devices
    public long addDevice(String name, String description) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_NAME, name);
        values.put(DatabaseHelper.CN_DESCRIPTION, description);
        return db.insert(DatabaseHelper.TABLE_DEVICES, null, values);
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
                devices.add(device);
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
        if(c == null) {
            return null;
        } else
            c.moveToFirst();

        device.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        device.setName(c.getString(c.getColumnIndex(DatabaseHelper.CN_NAME)));
        device.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        return device;
    }

    public int updateDevice(Long id, String name, String description) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_NAME, name);
        values.put(DatabaseHelper.CN_DESCRIPTION, description);
        return db.update(DatabaseHelper.TABLE_DEVICES, values, DatabaseHelper.CN_ID + " = " + id, null);
    }

    public void deleteDevice(Long id) {
        db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.CN_ID + " LIKE ?";
        String[] args = { String.valueOf(id) };
                db.delete(DatabaseHelper.TABLE_DEVICES, selection, args);
    }

    // actuators

    public long addActuator(String reference, String description, Long deviceId, String value) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_REFERENCE, reference);
        values.put(DatabaseHelper.CN_DESCRIPTION, description);
        values.put(DatabaseHelper.CN_DEVICE_ID, deviceId);
        values.put(DatabaseHelper.CN_VALUE, value);
        return db.insert(DatabaseHelper.TABLE_ACTUATORS, null, values);
    }

    public Actuator getActuator(Long id) {
        Actuator actuator = new Actuator();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_ACTUATORS + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c == null) {
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

        if(c.moveToFirst()) {
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

        if(c.moveToFirst()) {
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

    public int updateActuator(Long id, String reference, String description, Long deviceId, String value) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_REFERENCE, reference);
        values.put(DatabaseHelper.CN_DESCRIPTION, description);
        values.put(DatabaseHelper.CN_DEVICE_ID, deviceId);
        values.put(DatabaseHelper.CN_VALUE, value);
        return db.update(DatabaseHelper.TABLE_ACTUATORS, values, DatabaseHelper.CN_ID + " = " + id, null);
    }

    public void deleteActuator(Long id) {
        db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.CN_ID + " LIKE ?";
        String[] args = { String.valueOf(id) };
        db.delete(DatabaseHelper.TABLE_ACTUATORS, selection, args);
    }

    // sensors

    public long addSensor(String reference, String description, Long deviceId, String value) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_REFERENCE, reference);
        values.put(DatabaseHelper.CN_DESCRIPTION, description);
        values.put(DatabaseHelper.CN_DEVICE_ID, deviceId);
        values.put(DatabaseHelper.CN_VALUE, value);
        return db.insert(DatabaseHelper.TABLE_SENSORS, null, values);
    }

    public Sensor getSensor(Long id) {
        Sensor sensor = new Sensor();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_SENSORS + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c == null) {
            return null;
        } else
            c.moveToFirst();

        sensor.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        sensor.setReference(c.getString(c.getColumnIndex(DatabaseHelper.CN_REFERENCE)));
        sensor.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        sensor.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
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

        if(c.moveToFirst()) {
            do {
                Sensor sensor = new Sensor();
                sensor.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
                sensor.setReference(c.getString(c.getColumnIndex(DatabaseHelper.CN_REFERENCE)));
                sensor.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
                sensor.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
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

        if(c.moveToFirst()) {
            do {
                Sensor sensor = new Sensor();
                sensor.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
                sensor.setReference(c.getString(c.getColumnIndex(DatabaseHelper.CN_REFERENCE)));
                sensor.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
                sensor.setValue(c.getString(c.getColumnIndex(DatabaseHelper.CN_VALUE)));
                Device d = getDevice(c.getLong(c.getColumnIndex(DatabaseHelper.CN_DEVICE_ID)));
                sensor.setDevice(d);
                sensors.add(sensor);
            } while (c.moveToNext());
        }
        c.close();
        return sensors;
    }

    public int updateSensor(Long id, String reference, String description, Long deviceId, String value) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_REFERENCE, reference);
        values.put(DatabaseHelper.CN_DESCRIPTION, description);
        values.put(DatabaseHelper.CN_DEVICE_ID, deviceId);
        values.put(DatabaseHelper.CN_VALUE, value);
        return db.update(DatabaseHelper.TABLE_SENSORS, values, DatabaseHelper.CN_ID + " = " + id, null);
    }

    public void deleteSensor(Long id) {
        db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.CN_ID + " LIKE ?";
        String[] args = { String.valueOf(id) };
        db.delete(DatabaseHelper.TABLE_SENSORS, selection, args);
    }

    // actions

    public long addAction(String name, String description, String action, Long actuatorId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_NAME, name);
        values.put(DatabaseHelper.CN_DESCRIPTION, description);
        values.put(DatabaseHelper.CN_ACTION, action);
        values.put(DatabaseHelper.CN_ACTUATOR_ID, actuatorId);
        return db.insert(DatabaseHelper.TABLE_SENSORS, null, values);
    }

    public Action getAction(Long id) {
        Action action = new Action();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_ACTIONS + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c == null) {
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

        if(c.moveToFirst()) {
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

        if(c.moveToFirst()) {
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

    public int updateAction(Long id, String name, String description, String action, Long actuatorId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_NAME, name);
        values.put(DatabaseHelper.CN_DESCRIPTION, description);
        values.put(DatabaseHelper.CN_ACTION, action);
        values.put(DatabaseHelper.CN_ACTUATOR_ID, actuatorId);
        return db.update(DatabaseHelper.TABLE_ACTIONS, values, DatabaseHelper.CN_ID + " = " + id, null);
    }

    public void deleteAction(Long id) {
        db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.CN_ID + " LIKE ?";
        String[] args = { String.valueOf(id) };
        db.delete(DatabaseHelper.TABLE_ACTIONS, selection, args);
    }

    // sensorData

    public long addSensorData(LocalDateTime timestamp, String value, Long sensorId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CN_TIMESTAMP, timestamp.toString());
        values.put(DatabaseHelper.CN_VALUE, value);
        values.put(DatabaseHelper.CN_SENSOR_ID, sensorId);
        return db.insert(DatabaseHelper.TABLE_SENSOR_DATA, null, values);
    }

    //TODO datum sredi
   /* public SensorData getSensorData(Long id) {
        SensorData sensorData = new SensorData();
        String selectQuery = "select * from " + DatabaseHelper.TABLE_SENSOR_DATA + " where " + DatabaseHelper.CN_ID + " = " + id;
        Log.d("DATABASE QUERY", selectQuery);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c == null) {
            return null;
        } else
            c.moveToFirst();

        sensorData.setId(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ID)));
        sensorData.setTimestamp(LocalDateTime.parse(c.getString(c.getColumnIndex(DatabaseHelper.CN_TIMESTAMP))));
        action.setDescription(c.getString(c.getColumnIndex(DatabaseHelper.CN_DESCRIPTION)));
        action.setAction(c.getString(c.getColumnIndex(DatabaseHelper.CN_ACTION)));
        Actuator a = getActuator(c.getLong(c.getColumnIndex(DatabaseHelper.CN_ACTUATOR_ID)));
        action.setActuator(a);
        c.close();
        return action;
    }*/
}
