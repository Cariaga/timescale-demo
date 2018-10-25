package com.consol.labs.timescaledemo.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.consol.labs.timescaledemo.settings.EnvVarHelper;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(final String[] args) {
        LOGGER.info("starting producer");
        final ReadingsProducerSettings settings = new ReadingsProducerSettings(new EnvVarHelper());
        final ReadingsProducer producer = new ReadingsProducer(settings);
        producer.run();
    }
}
