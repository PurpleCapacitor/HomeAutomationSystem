package com.has.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private List<Device> sharedDevices = new ArrayList<>();

    public User() {
    }

    public User(Long id, String email, String password, String firstName, String lastName, List<Device> sharedDevices) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sharedDevices = sharedDevices;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Device> getSharedDevices() {
        return sharedDevices;
    }

    public void setSharedDevices(List<Device> sharedDevices) {
        this.sharedDevices = sharedDevices;
    }
}
