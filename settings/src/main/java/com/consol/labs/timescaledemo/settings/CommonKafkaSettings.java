package com.consol.labs.timescaledemo.settings;

import java.util.List;

public class CommonKafkaSettings {

    private final String kafkaBootstrapServersConfig;
    private final String kafkaClientIdConfig;
    private final List<String> kafkaTopics;

    public CommonKafkaSettings(final EnvVarHelper envVarHelper) {
        kafkaBootstrapServersConfig = envVarHelper.getSetting(EnvVar.KAFKA_BOOTSTRAP_SERVERS_CONFIG);
        kafkaClientIdConfig = envVarHelper.getSetting(EnvVar.KAFKA_CLIENT_ID_CONFIG);
        kafkaTopics = envVarHelper.getListOfSettings(EnvVar.KAFKA_TOPICS, EnvVar.STD_LIST_DELIMITER);
    }

    public String getKafkaBootstrapServersConfig() {
        return kafkaBootstrapServersConfig;
    }

    public String getKafkaClientIdConfig() {
        return kafkaClientIdConfig;
    }

    public List<String> getKafkaTopics() {
        return kafkaTopics;
    }
}
