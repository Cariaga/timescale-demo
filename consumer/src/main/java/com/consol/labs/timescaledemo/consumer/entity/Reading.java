package com.consol.labs.timescaledemo.consumer.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "t_reading")
@NamedQueries({ @NamedQuery(name = Reading.Q_GET_BY_FROM_TO,
        query = "SELECT r FROM Reading r WHERE :from <= r.readAt AND r.readAt <= :to"),
        @NamedQuery(name = Reading.Q_GET_BY_FROM_TO_AND_PERSON_ID,
                query = "SELECT r FROM Reading r WHERE :from <= r.readAt AND r.readAt <= :to AND r.person = :person") })
public class Reading {

    public static final String Q_GET_BY_FROM_TO = "Q_GET_BY_FROM_TO";
    public static final String Q_GET_BY_FROM_TO_AND_PERSON_ID = "Q_GET_BY_FROM_TO_AND_PERSON_ID";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "t_reading_fk_person_id"))
    private Person person;

    @Id
    @Column(name = "read_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant readAt;

    @Id
    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Id
    @Column(name = "description", nullable = false, length = 150)
    private String description;

    @Id
    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Id
    @Column(name = "unit", nullable = false, length = 300)
    private String unit;

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person personId) {
        this.person = personId;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(final Instant readAt) {
        this.readAt = readAt;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final UUID deviceId) {
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

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }
}
