package com.consol.labs.timescaledemo.consumer.manager;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.consol.labs.timescaledemo.consumer.dao.PersonDAO;
import com.consol.labs.timescaledemo.consumer.dao.ReadingDAO;
import com.consol.labs.timescaledemo.consumer.entity.AvgPulse;
import com.consol.labs.timescaledemo.consumer.entity.Person;
import com.consol.labs.timescaledemo.consumer.entity.Reading;
import com.consol.labs.timescaledemo.data.DeviceReading;
import com.consol.labs.timescaledemo.data.PersonReadings;

@Dependent
public class ReadingsDataManager {

    private static final Random RANDOM = new Random();

    private static final List<String> FIRST_NAMES =
            Arrays.asList("John", "Joe", "Jane", "Jake", "Mike", "Martha", "Agatha", "Bill");

    private static final List<String> LAST_NAMES =
            Arrays.asList("Doe", "Smith", "Miller", "Conners", "Bergmann", "Longmann");

    @Inject
    private PersonDAO personDAO;

    @Inject
    private ReadingDAO readingDAO;

    @Transactional
    public void saveReadings(final PersonReadings readings) {
        final List<DeviceReading> deviceReadings = readings.getDeviceReadings();
        if (deviceReadings == null || deviceReadings.isEmpty()) {
            return;
        }
        final Person person = personDAO
                .createOrUpdate(readings.getPersonId(), selectRandomName(FIRST_NAMES), selectRandomName(LAST_NAMES));
        deviceReadings.forEach(r -> readingDAO.createNew(person, r));
    }

    @Transactional
    public List<Reading> getReadings(final Optional<Long> personId, final Instant from, final Instant to) {
        final Optional<Person> person = getPerson(personId);
        if (person.isPresent()) {
            return readingDAO.getByFromToAndPersonId(from, to, person.get());
        } else {
            return readingDAO.getByFromTo(from, to);
        }
    }

    private Optional<Person> getPerson(final Optional<Long> id) {
        if (!id.isPresent()) {
            return Optional.empty();
        }
        return personDAO.find(id.get());
    }

    private static String selectRandomName(final List<String> names) {
        return names.get(RANDOM.nextInt(names.size()));
    }

    public List<AvgPulse> getAvgPulse(final long personId) {
        return personDAO.getAvgPulse(personId);
    }
}
