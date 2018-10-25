package com.consol.labs.timescaledemo.consumer.task;

import javax.inject.Inject;

import org.slf4j.Logger;

public abstract class DurableRunnable implements Runnable {

    @Inject
    protected Logger logger;

    @Override
    public void run() {
        try {
            logger.debug("started {}", getTaskName());
            execute();
            logger.debug("finished {}", getTaskName());
        } catch (final Throwable failure) {
            final String message = String.format("%s experienced failure", getTaskName());
            logger.error(message, failure);
            if (isRethrow(failure)) {
                logger.debug("rethrowing");
                throw new RuntimeException(message, failure);
            }
            logger.debug("no rethrow. continue after failure");
        }
    }

    protected abstract void execute() throws Throwable;

    protected String getTaskName() {
        return getClass().getName();
    }

    protected boolean isRethrow(final Throwable failure) {
        return false;
    }
}
