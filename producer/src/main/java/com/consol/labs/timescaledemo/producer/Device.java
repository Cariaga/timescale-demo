package com.consol.labs.timescaledemo.producer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.consol.labs.timescaledemo.data.DeviceReading;

public enum Device {

    VELOCITY(UUID.randomUUID(), "velocity", Unit.VELOCITY_METER_PER_SECOND),
    GPS_LONGITUDE(Id.LOCATION_SENSOR, "longitude", Unit.GPS_LONGITUDE),
    GPS_LATITUDE(Id.LOCATION_SENSOR, "latitude", Unit.GPS_LATITUDE),
    HEIGHT(Id.LOCATION_SENSOR, "height", Unit.METER),
    PULSE(UUID.randomUUID(), "pulse", Unit.PULSE),
    BODY_TEMPERATURE(UUID.randomUUID(), "body temperature", Unit.TEMPERATURE),
    ENVIRONMENT_TEMPERATURE(UUID.randomUUID(), "environment temperature", Unit.TEMPERATURE);

    private final UUID id;
    private final String description;
    private final String unit;

    Device(final UUID id, final String description, final String unit) {
        this.id = id;
        this.description = description;
        this.unit = unit;
    }

    public DeviceReading toDeviceReading(final double value) {
        return toDeviceReading(BigDecimal.valueOf(value));
    }

    public DeviceReading toDeviceReading(final BigDecimal value) {
        return new DeviceReading(Instant.now(), id, description, value, unit);
    }

    private static final class Id {
        private static final UUID LOCATION_SENSOR = UUID.randomUUID();
    }
}
