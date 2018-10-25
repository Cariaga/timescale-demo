package com.consol.labs.timescaledemo.producer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.consol.labs.timescaledemo.settings.CommonKafkaSettings;
import com.consol.labs.timescaledemo.settings.EnvVar;
import com.consol.labs.timescaledemo.settings.EnvVarHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadingsProducerSettings {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadingsProducerSettings.class);

    private final CommonKafkaSettings commonKafkaSettings;
    private final List<RoutePoint> route;
    private final long personId;

    public ReadingsProducerSettings(final EnvVarHelper envVarHelper) {
        this.commonKafkaSettings = new CommonKafkaSettings(envVarHelper);
        this.route = composeCircularRoute();
        this.personId = Long.parseLong(envVarHelper.getSetting(EnvVar.PRODUCER_PERSON_ID));
    }

    public CommonKafkaSettings getCommonKafkaSettings() {
        return commonKafkaSettings;
    }

    public List<RoutePoint> getRoute() {
        return route;
    }

    public long getPersonId() {
        return personId;
    }

    private static List<RoutePoint> composeCircularRoute() {
        final List<RoutePoint> fromAtoB = readRoute("consol-belsenplatz-jcon.geojson");
        final List<RoutePoint> fromBtoA = readRoute("jcon-to-consol.geojson");
        final List<RoutePoint> result = new ArrayList<>(fromAtoB.size() + fromBtoA.size());
        result.addAll(fromAtoB);
        result.addAll(fromBtoA);
        return Collections.unmodifiableList(result);
    }

    private static List<RoutePoint> readRoute(final String fileName) {
        final InputStream stream = ReadingsProducerSettings.class.getClassLoader()
                .getResourceAsStream(String.format("route/%s", fileName));
        if (stream == null) {
            throw new RuntimeException("expected route file: " + fileName);
        }
        final JsonNode rootNode;
        try {
            rootNode = (new ObjectMapper()).readTree(stream);
        } catch (final IOException e) {
            final String message = String.format("failed to parse route file %s", fileName);
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
        final JsonNode coordinates = rootNode.get("geometry").withArray("coordinates");
        final List<RoutePoint> result = new ArrayList<>();
        for (final JsonNode coordinate : coordinates) {
            final double longitude = coordinate.get(0).asDouble();
            final double latitude = coordinate.get(1).asDouble();
            final double height = coordinate.get(2).asDouble();
            result.add(new RoutePoint(longitude, latitude, height));
        }
        return Collections.unmodifiableList(result);
    }
}
