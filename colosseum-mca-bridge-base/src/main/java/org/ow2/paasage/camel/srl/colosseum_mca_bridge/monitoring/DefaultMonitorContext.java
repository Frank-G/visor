/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 21.01.15.
 */
public class DefaultMonitorContext implements MonitorContext {

    public static final String LOCAL_IP = "local_ip";
    private final Map<String, String> context;

    public static MonitorContextBuilder builder() {
        return new MonitorContextBuilder();
    }

    private DefaultMonitorContext(Map<String, String> context) {
        this.context = context;
    }

    @Override public String getValue(String context) {
        return this.context.get(context);
    }

    @Override public boolean hasValue(String context) {
        return this.context.containsKey(context);
    }

    @Override public String getOrDefault(String context, String defaultValue) {
        return this.context.getOrDefault(context, defaultValue);
    }

    @Override public Map<String, String> getContext() {
        return context;
    }

    @Override public String toString() {
        StringBuilder contextString = new StringBuilder("[");
        for (Map.Entry<String, String> mapEntry : this.getContext().entrySet()) {
            contextString.append(mapEntry.getKey());
            contextString.append(": ");
            contextString.append(mapEntry.getValue());
            contextString.append(",");
        }
        contextString.append("]");
        return String.format("MonitorContext(Context: %s)", contextString.toString());
    }

    public static class MonitorContextBuilder {

        private final Map<String, String> map;

        public MonitorContextBuilder() {
            map = new HashMap<>();
        }

        public MonitorContextBuilder addContext(String context, String value) {
            this.map.put(context, value);
            return this;
        }

        public MonitorContextBuilder addContext(Map<String, String> context) {
            this.map.putAll(context);
            return this;
        }

        public MonitorContext build() {
            return new DefaultMonitorContext(ImmutableMap.copyOf(this.map));
        }
    }

}
