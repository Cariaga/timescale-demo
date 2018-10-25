package com.consol.labs.timescaledemo.consumer.startup;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;

import com.consol.labs.timescaledemo.consumer.task.KafkaConsumerTask;

@Singleton
@Startup
public class KafkaConsumerTaskStarter {

    @Resource
    private ManagedScheduledExecutorService executorService;

    @Inject
    private KafkaConsumerTask kafkaConsumerTask;

    @PostConstruct
    public void initConsumer() {
        executorService.scheduleAtFixedRate(kafkaConsumerTask, 0, 10, TimeUnit.SECONDS);
    }
}
