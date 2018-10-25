package com.consol.labs.timescaledemo.consumer.task;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.consol.labs.timescaledemo.common.DurabilityUtils;
import com.consol.labs.timescaledemo.consumer.config.AppSettings;
import com.consol.labs.timescaledemo.consumer.manager.ReadingsDataManager;
import com.consol.labs.timescaledemo.data.PersonReadings;
import com.fasterxml.jackson.databind.ObjectMapper;

@Dependent
public class KafkaConsumerTask extends DurableRunnable implements Closeable {

    @Inject
    private AppSettings settings;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ReadingsDataManager dataManager;

    private final AtomicBoolean closed = new AtomicBoolean();
    private Consumer<Long, String> consumer;

    @Override
    protected void execute() throws Throwable {
        if (!isConsumerCreated()) {
            logger.error("failed to create consumer");
            return;
        }
        logger.info("consumer has been created");
        try {
            while (!closed.get()) {
                final ConsumerRecords<Long, String> records = consumer.poll(Duration.ofMillis(Long.MAX_VALUE));
                for (final ConsumerRecord<Long, String> record : records) {
                    tryParseReceivedReadings(record.value()).ifPresent(dataManager::saveReadings);
                }
                consumer.commitSync();
            }
        } catch (final WakeupException e) {
            logger.error("received wake up signal. will close", e);
        } finally {
            consumer.close();
        }
    }

    @PreDestroy
    public void cleanup() {
        close();
    }

    @Override
    public void close() {
        closed.set(true);
    }

    private Optional<PersonReadings> tryParseReceivedReadings(final String rawReadings) {
        try {
            final PersonReadings readings = objectMapper.readValue(rawReadings, PersonReadings.class);
            return Optional.ofNullable(readings);
        } catch (final IOException e) {
            logger.error("failed to parse readings: " + rawReadings, e);
            return Optional.empty();
        }
    }

    private boolean isConsumerCreated() {
        final Optional<Consumer<Long, String>> c = createConsumer();
        if (!c.isPresent()) {
            close();
            return false;
        }
        consumer = c.get();
        return true;
    }

    private Optional<Consumer<Long, String>> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                settings.getCommonKafkaSettings().getKafkaBootstrapServersConfig());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, settings.getCommonKafkaSettings().getKafkaClientIdConfig());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, settings.getKafkaGroupIdConfig());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        final Supplier<Consumer<Long, String>> supplier = () -> {
            final Consumer<Long, String> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(settings.getCommonKafkaSettings().getKafkaTopics());
            return consumer;
        };
        return DurabilityUtils.getWithRetry(supplier, Duration.ofMinutes(1));
    }
}
