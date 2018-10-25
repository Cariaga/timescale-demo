package com.consol.labs.timescaledemo.consumer.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.consol.labs.timescaledemo.settings.CommonKafkaSettings;
import com.consol.labs.timescaledemo.settings.EnvVar;
import com.consol.labs.timescaledemo.settings.EnvVarHelper;

@Singleton
public class AppSettings {

    @Inject
    private Logger logger;

    private CommonKafkaSettings commonKafkaSettings;
    private String kafkaGroupIdConfig;

    @PostConstruct
    public void init() {
        final EnvVarHelper envVarHelper = new EnvVarHelper();
        logger.info("read {} environment variables", envVarHelper.getEnvVarCount());
        commonKafkaSettings = new CommonKafkaSettings(envVarHelper);
        kafkaGroupIdConfig = envVarHelper.getSetting(EnvVar.CONSUMER_KAFKA_GROUP_ID_CONFIG);
    }

    public CommonKafkaSettings getCommonKafkaSettings() {
        return commonKafkaSettings;
    }

    public String getKafkaGroupIdConfig() {
        return kafkaGroupIdConfig;
    }
}
