package com.has.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "HomeAutomation.db";
    public static final int DB_VERSION = 1;


    public static final String TABLE_DEVICES = "devices";
    public static final String TABLE_ACTUATORS = "actuators";
    public static final String TABLE_SENSORS = "sensors";
    public static final String TABLE_ACTIONS = "actions";
    public static final String TABLE_SENSOR_DATA = "sensor_data";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_RULES = "rules";
    public static final String TABLE_USERS_DEVICES = "users_devices";

    public static final String CN_ID = "id";
    public static final String CN_NAME = "name";
    public static final String CN_DESCRIPTION = "description";
    public static final String CN_REFERENCE = "reference";
    public static final String CN_DEVICE_ID = "device_id";
    public static final String CN_VALUE = "value";
    public static final String CN_ACTION = "actuator_action";
    public static final String CN_ACTUATOR_ID = "actuator_id";
    public static final String CN_TIMESTAMP = "timestamp";
    public static final String CN_SENSOR_ID = "sensor_id";
    public static final String CN_EMAIL = "email";
    public static final String CN_PASSWORD = "password";
    public static final String CN_FIRST_NAME = "first_name";
    public static final String CN_LAST_NAME = "last_name";
    public static final String CN_SHARED = "shared";
    public static final String CN_USER_ID = "user_id";


    private static final String CREATE_TABLE_DEVICES = "create table " + TABLE_DEVICES + "(" + CN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + CN_NAME + " TEXT NOT NULL, " + CN_DESCRIPTION + " TEXT);";

    private static final String CREATE_TABLE_ACTUATORS = "create table " + TABLE_ACTUATORS + " (" + CN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + CN_REFERENCE + " TEXT NOT NULL, " + CN_VALUE +
            " TEXT NOT NULL, " + CN_DESCRIPTION + " TEXT NOT NULL, "+
            CN_DEVICE_ID + " INTEGER, " + "FOREIGN KEY (" + CN_DEVICE_ID + ") REFERENCES " +
            TABLE_DEVICES + " (" + CN_ID + ") );";

    public static final String CREATE_TABLE_SENSORS = "create table " + TABLE_SENSORS + " (" + CN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + CN_REFERENCE + " TEXT NOT NULL, " + CN_VALUE +
            " TEXT NOT NULL, " + CN_DESCRIPTION + " TEXT NOT NULL, "+
            CN_DEVICE_ID + " INTEGER, " + "FOREIGN KEY (" + CN_DEVICE_ID + ") REFERENCES " +
            TABLE_DEVICES + " (" + CN_ID + ") );";

    public static final String CREATE_TABLE_ACTIONS = "create table " + TABLE_ACTIONS + " (" + CN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + CN_NAME + " TEXT NOT NULL, " + CN_ACTION + " TEXT NOT NULL, " +
            CN_DESCRIPTION + " TEXT NOT NULL, " + CN_ACTUATOR_ID + " INTEGER, " +
            "FOREIGN KEY (" + CN_ACTUATOR_ID + ") REFERENCES " + TABLE_ACTUATORS + " (" + CN_ID + ") );";

    public static final String CREATE_TABLE_SENSOR_DATA = "create table " + TABLE_SENSOR_DATA + " (" + CN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + CN_TIMESTAMP + " TEXT NOT NULL, " + CN_VALUE + " TEXT NOT NULL, " +
             CN_SENSOR_ID + " INTEGER, " +
            "FOREIGN KEY (" + CN_SENSOR_ID + ") REFERENCES " + TABLE_SENSORS + " (" + CN_ID + ") );";

    //znaci 3 tabele users devices i users_devices
    //u toj trecoj imas user id, device id i shared (true false) i onda ces taj shared kad ti zatreba za shared devices
    public static final String CREATE_TABLE_USERS = "create table " + TABLE_USERS + " (" + CN_EMAIL +
            " TEXT PRIMARY KEY, " + CN_PASSWORD + " TEXT NOT NULL, " + CN_FIRST_NAME + " TEXT NOT NULL, " +
            CN_LAST_NAME + " TEXT NOT NULL);";

    public static final String CREATE_TABLE_USERS_DEVICES = "create table " + TABLE_USERS_DEVICES + " (" +
            CN_USER_ID + " INTEGER, " + CN_DEVICE_ID + " INTEGER, " + CN_SHARED + " INTEGER);";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DEVICES);
        db.execSQL(CREATE_TABLE_ACTUATORS);
        db.execSQL(CREATE_TABLE_SENSORS);
        db.execSQL(CREATE_TABLE_ACTIONS);
        db.execSQL(CREATE_TABLE_SENSOR_DATA);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_USERS_DEVICES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_DEVICES + "';");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_ACTUATORS + "';");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_SENSORS + "';");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_ACTIONS + "';");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_SENSOR_DATA + "';");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_USERS + "';");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_USERS_DEVICES + "';");
        onCreate(db);
    }
}
