package com.consol.labs.timescaledemo.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class EnvVarHelper {

    private final Map<String, String> envVars = Collections.unmodifiableMap(new HashMap<>(System.getenv()));

    public int getEnvVarCount() {
        return envVars.size();
    }

    public List<String> getListOfSettings(final String key, final String delimiter) {
        final String value = getSetting(key);
        final String[] parts = value.split(delimiter);
        if (parts.length == 0) {
            throw new RuntimeException("expected list for setting: " + key);
        }
        final List<String> result = new ArrayList<>(parts.length);
        for (final String part : parts) {
            if (StringUtils.isBlank(part)) {
                throw new RuntimeException("empty element in list: " + key);
            }
            result.add(part);
        }
        return Collections.unmodifiableList(result);
    }

    public String getSetting(final String key) {
        final String value = envVars.get(key);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        throw new RuntimeException("expected setting: " + key);
    }
}
