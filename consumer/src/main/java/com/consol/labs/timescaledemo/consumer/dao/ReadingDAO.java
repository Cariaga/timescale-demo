package com.consol.labs.timescaledemo.consumer.dao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import com.consol.labs.timescaledemo.consumer.entity.Person;
import com.consol.labs.timescaledemo.consumer.entity.Reading;
import com.consol.labs.timescaledemo.data.DeviceReading;

@Dependent
public class ReadingDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public Reading createNew(final Person person, final DeviceReading deviceReading) {
        final Reading entity = new Reading();
        entity.setPerson(person);
        entity.setDeviceId(deviceReading.getDeviceId());
        entity.setDescription(deviceReading.getDescription());
        entity.setReadAt(deviceReading.getReadAt());
        entity.setUnit(deviceReading.getUnit());
        entity.setValue(deviceReading.getValue());
        entityManager.persist(entity);
        return entity;
    }

    public List<Reading> getByFromTo(final Instant from, final Instant to) {
        final List<Reading> result =
                entityManager.createNamedQuery(Reading.Q_GET_BY_FROM_TO, Reading.class).setParameter("from", from)
                        .setParameter("to", to).getResultList();
        return Collections.unmodifiableList(result);
    }

    public List<Reading> getByFromToAndPersonId(final Instant from, final Instant to, final Person person) {
        final List<Reading> result =
                entityManager.createNamedQuery(Reading.Q_GET_BY_FROM_TO_AND_PERSON_ID, Reading.class)
                        .setParameter("from", from).setParameter("to", to).setParameter("person", person)
                        .getResultList();
        return Collections.unmodifiableList(result);
    }

    public List<BigDecimal> getPosition(final long personId) {
        final StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_last_position")
                .registerStoredProcedureParameter("in_person_id", Long.class, ParameterMode.IN)
                .registerStoredProcedureParameter("out_latitude", BigDecimal.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("out_longitude", BigDecimal.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("out_height", BigDecimal.class, ParameterMode.OUT);

        query.setParameter("in_person_id", personId).execute();

        final BigDecimal latitude = (BigDecimal) query.getOutputParameterValue("out_latitude");
        final BigDecimal longitude = (BigDecimal) query.getOutputParameterValue("out_longitude");
        final BigDecimal height = (BigDecimal) query.getOutputParameterValue("out_height");
        return Arrays.asList(latitude, longitude, height);
    }
}
