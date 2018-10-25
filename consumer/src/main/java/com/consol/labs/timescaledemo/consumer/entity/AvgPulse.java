package com.consol.labs.timescaledemo.consumer.entity;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "v_avg_pulse")
@NamedQueries({ @NamedQuery(name = AvgPulse.Q_GET_FOR_PERSON,
        query = "SELECT ap FROM AvgPulse ap WHERE ap.personId = :personId") })
public class AvgPulse {

    public static final String Q_GET_FOR_PERSON = "Q_GET_FOR_PERSON";

    @Id
    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Id
    @Column(name = "t", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant time;

    @Column(name = "avg_pulse", nullable = false)
    private BigDecimal pulse;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(final Long personId) {
        this.personId = personId;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(final Instant time) {
        this.time = time;
    }

    public BigDecimal getPulse() {
        return pulse;
    }

    public void setPulse(final BigDecimal pulse) {
        this.pulse = pulse;
    }
}
