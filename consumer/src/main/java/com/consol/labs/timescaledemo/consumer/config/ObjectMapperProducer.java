package com.consol.labs.timescaledemo.consumer.config;

import javax.enterprise.inject.Produces;

import com.consol.labs.timescaledemo.data.ObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProducer {

    @Produces
    public ObjectMapper getObjectMapper() {
        return ObjectMapperFactory.getObjectMapper();
    }
}
