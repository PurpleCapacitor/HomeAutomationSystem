package com.has.model;

public class Rule {

    private Long id;
    private String name;
    private String description;
    private String value;
    private String ruleRelation;
    private String valueActuator;
    private Sensor sensor;
    private Actuator actuator;
    private User user;
    private Long versionTimestamp;


    public Rule() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRuleRelation() {
        return ruleRelation;
    }

    public void setRuleRelation(String ruleRelation) {
        this.ruleRelation = ruleRelation;
    }

    public String getValueActuator() {
        return valueActuator;
    }

    public void setValueActuator(String valueActuator) {
        this.valueActuator = valueActuator;
    }

    public Long getVersionTimestamp() {
        return versionTimestamp;
    }

    public void setVersionTimestamp(Long versionTimestamp) {
        this.versionTimestamp = versionTimestamp;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Actuator getActuator() {
        return actuator;
    }

    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Rule(String name, String description, String value, String ruleRelation, String valueActuator) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.ruleRelation = ruleRelation;
        this.valueActuator = valueActuator;
    }
}
