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
public class MetricBuilder {

    private String name;
    private Object value;
    private long timestamp;
    private final Map<String, String> tags;

    MetricBuilder() {
        tags = new HashMap<>();
    }

    public static MetricBuilder newBuilder() {
        return new MetricBuilder();
    }

    public MetricBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MetricBuilder value(Object value) {
        this.value = value;
        return this;
    }

    public MetricBuilder timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public MetricBuilder addTag(String key, String value) {
        this.tags.put(key, value);
        return this;
    }

    public MetricBuilder addTags(Map<String, String> tags) {
        this.tags.putAll(tags);
        return this;
    }

    public Metric build() {
        return new MetricImpl(name, value, timestamp, ImmutableMap.copyOf(this.tags));
    }
}
