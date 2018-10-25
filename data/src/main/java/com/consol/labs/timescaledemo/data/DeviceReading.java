package com.consol.labs.timescaledemo.data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceReading {

    @JsonProperty(value = "readAt", required = true)
    private Instant readAt;

    @JsonProperty(value = "deviceId", required = true)
    private UUID deviceId;

    @JsonProperty(value = "description", required = true)
    private String description;

    @JsonProperty(value = "value", required = true)
    private BigDecimal value;

    @JsonProperty(value = "unit", required = true)
    private String unit;

    public DeviceReading() {
    }

    public DeviceReading(Instant readAt, UUID deviceId, String description, BigDecimal value, String unit) {
        this.readAt = readAt;
        this.deviceId = deviceId;
        this.description = description;
        this.value = value;
        this.unit = unit;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
