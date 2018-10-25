package com.consol.labs.timescaledemo.common;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DurabilityUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DurabilityUtils.class);

    private DurabilityUtils() {
    }

    public static <T> Optional<T> getWithRetry(final Supplier<T> supplier, final Duration sleepDuration) {
        return getWithRetry(supplier, sleepDuration, e -> false);
    }

    public static <T> Optional<T> getWithRetry(final Supplier<T> supplier, final Duration sleepDuration,
            final Predicate<? super Throwable> isStopRetrying) {
        while (true) {
            try {
                return Optional.of(supplier.get());
            } catch (final Throwable e) {
                if (isStopRetrying.test(e)) {
                    LOGGER.error("failure. will not retry", e);
                    return Optional.empty();
                }
                LOGGER.error("failure. will retry", e);
                try {
                    Thread.sleep(sleepDuration.toMillis());
                } catch (final InterruptedException interrupted) {
                    LOGGER.error("interrupted. will not retry", interrupted);
                    return Optional.empty();
                }
            }
        }
    }
}
