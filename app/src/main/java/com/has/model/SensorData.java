package com.has.model;

import java.time.LocalDateTime;

public class SensorData {

    private Long id;
    private LocalDateTime timestamp;
    private String value;
    private Sensor sensor;

    public SensorData() {
    }

    public SensorData(Long id, LocalDateTime timestamp, String value, Sensor sensor) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
        this.sensor = sensor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
