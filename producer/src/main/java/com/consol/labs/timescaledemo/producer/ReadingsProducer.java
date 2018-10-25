package com.consol.labs.timescaledemo.producer;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.consol.labs.timescaledemo.common.DurabilityUtils;
import com.consol.labs.timescaledemo.data.ObjectMapperFactory;
import com.consol.labs.timescaledemo.data.PersonReadings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadingsProducer implements Runnable {

    private static final double EPSILON = 1e-1;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Random random = new Random();
    private final ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
    private final ReadingsProducerSettings settings;

    public ReadingsProducer(final ReadingsProducerSettings settings) {
        this.settings = settings;
    }

    @Override
    public void run() {
        final Optional<Producer<Long, String>> producer = createProducer();
        if (!producer.isPresent()) {
            logger.warn("failed to create producer");
            return;
        }
        logger.info("producer has been created");
        try (final Producer<Long, String> actualProducer = producer.get()) {
            final String topic = settings.getCommonKafkaSettings().getKafkaTopics().get(0);
            RoutePoint currentPosition = settings.getRoute().get(0);
            int nextPointIndex = 1;
            int lastSleepTimeMillis = 0;
            while (true) {
                try {
                    final double randomVelocity = 1.0 + 5 * random.nextDouble();
                    final Pair<RoutePoint, Integer> p =
                            updateCurrentPositionAndNextPointIndex(currentPosition, nextPointIndex, randomVelocity,
                                    lastSleepTimeMillis);
                    currentPosition = p.getLeft();
                    nextPointIndex = p.getRight();
                    final PersonReadings readings = getPersonReadings(currentPosition, randomVelocity);
                    final ProducerRecord<Long, String> record =
                            new ProducerRecord<>(topic, mapper.writeValueAsString(readings));
                    actualProducer.send(record);
                    actualProducer.flush();
                    logger.debug("sent readings");
                    lastSleepTimeMillis = 500 + random.nextInt(2500);
                    logger.debug("sleep {} seconds", lastSleepTimeMillis);
                    Thread.sleep(lastSleepTimeMillis);
                } catch (final RuntimeException | JsonProcessingException e) {
                    logger.error("failure. will retry", e);
                } catch (final InterruptedException e) {
                    logger.error("interrupted. exiting", e);
                    return;
                }
            }
        }
    }

    private PersonReadings getPersonReadings(final RoutePoint currentPosition, final double velocity) {
        final PersonReadings readings = new PersonReadings();
        readings.setPersonId(settings.getPersonId());
        readings.getDeviceReadings().add(Device.VELOCITY.toDeviceReading(velocity));
        readings.getDeviceReadings().add(Device.GPS_LONGITUDE.toDeviceReading(currentPosition.getX()));
        readings.getDeviceReadings().add(Device.GPS_LATITUDE.toDeviceReading(currentPosition.getY()));
        readings.getDeviceReadings().add(Device.HEIGHT.toDeviceReading(currentPosition.getZ()));
        final double pulse = 10 * velocity + 50 + 5 * random.nextDouble();
        readings.getDeviceReadings().add(Device.PULSE.toDeviceReading(pulse));
        final double bodyTemperature = 37 + (velocity / 10) * random.nextDouble();
        readings.getDeviceReadings().add(Device.BODY_TEMPERATURE.toDeviceReading(bodyTemperature));
        final double environmentTemperature = 20 + 3 * (0.5 - random.nextDouble());
        readings.getDeviceReadings().add(Device.ENVIRONMENT_TEMPERATURE.toDeviceReading(environmentTemperature));
        return readings;
    }

    private Pair<RoutePoint, Integer> updateCurrentPositionAndNextPointIndex(RoutePoint currentPosition,
            int nextPointIndex, final double randomVelocity, final int lastSleepTimeMillis) {
        double distance = randomVelocity * (lastSleepTimeMillis / 1000.0);
        final List<RoutePoint> route = settings.getRoute();
        while (distance >= currentPosition.distanceTo(route.get(nextPointIndex))) {
            distance -= currentPosition.distanceTo(route.get(nextPointIndex));
            currentPosition = route.get(nextPointIndex);
            nextPointIndex = (nextPointIndex + 1) % route.size();
        }
        if (distance <= EPSILON) {
            return Pair.of(currentPosition, nextPointIndex);
        }
        return Pair.of(currentPosition.move(route.get(nextPointIndex), distance), nextPointIndex);
    }

    private Optional<Producer<Long, String>> createProducer() {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                settings.getCommonKafkaSettings().getKafkaBootstrapServersConfig());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, settings.getCommonKafkaSettings().getKafkaClientIdConfig());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        final Supplier<Producer<Long, String>> supplier = () -> new KafkaProducer<>(props);
        return DurabilityUtils.getWithRetry(supplier, Duration.ofMinutes(1));
    }
}
