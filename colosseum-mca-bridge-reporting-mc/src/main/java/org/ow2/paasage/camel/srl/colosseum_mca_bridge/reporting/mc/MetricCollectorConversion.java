/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.mc;

import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;

/**
 * Created by Frank on 03.08.2015.
 */
public class MetricCollectorConversion {
    private final double value;
    private final String id;
    private final long timestamp;
    private final boolean isEvent;

    public MetricCollectorConversion(Metric metric) throws McMetricConversionException {
        final int split = metric.getName().indexOf("#");
        if (split == -1) {
            throw new McMetricConversionException("No split char in: " + metric.getName());
        }
        if(metric.getName().substring(0, split).equals("cdo_metric")) {
            isEvent = false;
        } else if (metric.getName().substring(0, split).equals("cdo_event")){
            isEvent = true;
        } else {
            throw new McMetricConversionException("Wrong metric type: " + metric.getName().substring(split));
        }
        id = metric.getName().substring(split + 1);

        //TODO do this more safe
        value = Double.parseDouble((String)metric.getValue());
        timestamp = metric.getTimestamp();
    }

    public double getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isEvent() {
        return isEvent;
    }
}
