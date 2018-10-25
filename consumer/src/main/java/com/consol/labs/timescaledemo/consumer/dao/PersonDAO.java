package com.consol.labs.timescaledemo.consumer.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.consol.labs.timescaledemo.consumer.entity.AvgPulse;
import com.consol.labs.timescaledemo.consumer.entity.Person;

@Dependent
public class PersonDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public Person createOrUpdate(final long id, final String firstName, final String lastName) {
        final Person entity = new Person();
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setId(id);
        return entityManager.merge(entity);
    }

    public Optional<Person> find(final long id) {
        return Optional.ofNullable(entityManager.find(Person.class, id));
    }

    public List<AvgPulse> getAvgPulse(final long personId) {
        final List<AvgPulse> result = entityManager
                .createNamedQuery(AvgPulse.Q_GET_FOR_PERSON, AvgPulse.class)
                .setParameter("personId", personId)
                .setMaxResults(100)
                .getResultList();
        return Collections.unmodifiableList(result);
    }
}
