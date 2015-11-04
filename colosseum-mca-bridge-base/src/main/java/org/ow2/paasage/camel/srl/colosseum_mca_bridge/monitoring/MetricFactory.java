/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

/**
 * Created by daniel on 06.02.15.
 */
public class MetricFactory {

    private MetricFactory() {

    }

    public static Metric from(String metricName, Measurement measurement,
        MonitorContext monitorContext) {
        return MetricBuilder.newBuilder().name(metricName).value(measurement.getValue())
            .timestamp(measurement.getTimestamp()).addTags(monitorContext.getContext()).build();
    }

}
