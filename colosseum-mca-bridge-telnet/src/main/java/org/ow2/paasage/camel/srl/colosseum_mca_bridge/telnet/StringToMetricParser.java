/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.telnet;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.DefaultMonitorContext;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.MetricBuilder;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 16.12.14.
 */
public class StringToMetricParser implements RequestParsingInterface<String, Metric> {

    private final String localIp;

    @Inject public StringToMetricParser(@Named("localIp") String localIp) {
        this.localIp = localIp;
    }

    @Override public Metric parse(String s) throws ParsingException {
        checkNotNull(s);

        final String[] parts = s.split(" ");
        if (parts.length != 4) {
            throw new ParsingException("Expected 4 strings, got " + parts.length);
        }
        final String applicationName = parts[0];
        final String metricName = parts[1];
        final String value = parts[2];
        long timestamp;
        try {
            timestamp = Long.parseLong(parts[3]);
        } catch (NumberFormatException e) {
            throw new ParsingException("Could not convert 4th string to long.");
        }

        return MetricBuilder.newBuilder().name(metricName).value(value).timestamp(timestamp)
            .addTag("application", applicationName)
            .addTag(DefaultMonitorContext.LOCAL_IP, this.localIp).build();
    }
}
