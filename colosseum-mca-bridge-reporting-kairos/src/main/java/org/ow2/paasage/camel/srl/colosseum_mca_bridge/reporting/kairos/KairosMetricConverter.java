/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.kairos;


import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;
import org.kairosdb.client.builder.MetricBuilder;

import java.util.Map;

/**
 * Created by daniel on 23.09.14.
 */
public class KairosMetricConverter {

    private final MetricBuilder metricBuilder;

    public KairosMetricConverter() {
        this.metricBuilder = MetricBuilder.getInstance();
    }

    public KairosMetricConverter add(Metric metric) throws KairosMetricConversionException {
        org.kairosdb.client.builder.Metric kairosMetric = metricBuilder.addMetric(metric.getName())
            .addDataPoint(metric.getTimestamp(), metric.getValue());

        //workaround for https://github.com/kairosdb/kairosdb-client/issues/27
        //manually add single tags as addTags() is broken.
        for (final Map.Entry<String, String> entry : metric.getTags().entrySet()) {
            kairosMetric.addTag(entry.getKey(), entry.getValue());
        }
        return this;
    }


    public MetricBuilder convert() {
        return metricBuilder;
    }


}
