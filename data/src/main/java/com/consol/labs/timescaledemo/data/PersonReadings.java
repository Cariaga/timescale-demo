package com.consol.labs.timescaledemo.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonReadings {

    @JsonProperty(value = "personId", required = true)
    private long personId;

    @JsonProperty(value = "deviceReadings", required = true)
    private List<DeviceReading> deviceReadings = new ArrayList<>();

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(final long personId) {
        this.personId = personId;
    }

    public List<DeviceReading> getDeviceReadings() {
        return deviceReadings;
    }

    public void setDeviceReadings(final List<DeviceReading> deviceReadings) {
        this.deviceReadings = deviceReadings;
    }
}
