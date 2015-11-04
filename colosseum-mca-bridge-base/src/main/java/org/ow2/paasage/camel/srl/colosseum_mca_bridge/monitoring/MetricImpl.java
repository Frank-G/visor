/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A basic metric for the system.
 * <p>
 * A metric always consists of:
 * - a name: the name of the metric
 * - a value: a value for this metric.
 * - a timestamp: the unix timestamp when this metric was taken.
 * - a map of key value tags
 * <p>
 * <p>
 * Use MetricFactory to create metrics.
 *
 * @see MetricFactory
 */
public class MetricImpl implements Metric {

    protected final String name;

    protected final Object value;

    protected final long timestamp;

    protected final Map<String, String> tags;

    /**
     * Constructor for the metric.
     *
     * @param name      the name of the metric.
     * @param value     the value of the metric.
     * @param timestamp the timestamp of the metric.
     * @param tags      tags for the metric.
     */
    MetricImpl(String name, Object value, long timestamp, Map<String, String> tags) {
        checkNotNull(name);
        checkNotNull(value);
        checkNotNull(timestamp);
        checkNotNull(tags);
        checkArgument(!name.isEmpty(), "Name must not be empty.");
        checkArgument(timestamp > 0, "Timestamp must be > 0");

        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    /**
     * Getter for the name of the metric.
     *
     * @return the name of the metric.
     */
    @Override public String getName() {
        return name;
    }

    /**
     * Getter for the value of the metric.
     *
     * @return the value of the metric.
     */
    @Override public Object getValue() {
        return value;
    }

    /**
     * Getter for the timestamp of the metric.
     *
     * @return the time the metric was taken.
     */
    @Override public long getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for the tags of the metric.
     *
     * @return tags for the metric.
     */
    @Override public Map<String, String> getTags() {
        return tags;
    }

    /**
     * To String method for the metric.
     *
     * @return the metric as string representation, mainly for logging purposes.
     */
    @Override public String toString() {
        StringBuilder tagsString = new StringBuilder("[");
        for (Map.Entry<String, String> mapEntry : this.getTags().entrySet()) {
            tagsString.append(mapEntry.getKey());
            tagsString.append(": ");
            tagsString.append(mapEntry.getValue());
            tagsString.append(",");
        }
        tagsString.append("]");
        return String.format("Metric(Name: %s, Value: %s, Time: %s, Tags: %s)", this.getName(),
            this.getValue(), this.getTimestamp(), tagsString.toString());
    }
}
