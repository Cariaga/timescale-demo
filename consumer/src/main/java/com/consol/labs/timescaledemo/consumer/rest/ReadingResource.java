package com.consol.labs.timescaledemo.consumer.rest;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.consol.labs.timescaledemo.consumer.entity.AvgPulse;
import com.consol.labs.timescaledemo.consumer.entity.Reading;
import com.consol.labs.timescaledemo.consumer.manager.ReadingsDataManager;

@Path("reading")
public class ReadingResource {

    @Inject
    private ReadingsDataManager dataManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Reading> getReadings(@QueryParam("personId") final Long personId,
            @QueryParam("fromEpochMilli") final Long fromEpochMilli,
            @QueryParam("toEpochMilli") final Long toEpochMilli) {
        final Instant to = toEpochMilli == null ? Instant.now() : Instant.ofEpochMilli(toEpochMilli);
        final Instant from =
                fromEpochMilli == null ? to.minus(Duration.ofHours(1)) : Instant.ofEpochMilli(fromEpochMilli);
        return dataManager.getReadings(Optional.ofNullable(personId), from, to);
    }

    @Path("avg_pulse")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AvgPulse> getAvgPulse(@QueryParam("personId") final Long personId) {
        if (personId == null) {
            return Collections.emptyList();
        }
        return dataManager.getAvgPulse(personId);
    }

    // does not work:
    // combo EclipseLink + JDBC postgres driver
    // cannot interface with functions having multiple OUT parameters
    @Path("position")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<BigDecimal> getPosition(@QueryParam("personId") final Long personId) {
        if (personId == null) {
            return Collections.emptyList();
        }
        return dataManager.getPosition(personId);
    }
}
