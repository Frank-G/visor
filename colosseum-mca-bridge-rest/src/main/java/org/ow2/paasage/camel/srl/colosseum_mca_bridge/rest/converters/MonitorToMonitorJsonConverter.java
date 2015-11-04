/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.converters;


import org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.resources.BaseMonitor;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.resources.Links;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.resources.MonitorEntity;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.resources.MonitorWithLinks;

/**
 * Created by daniel on 09.02.15.
 */
public class MonitorToMonitorJsonConverter
    implements OneWayConverter<org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Monitor, MonitorEntity> {

    @Override public MonitorEntity apply(org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Monitor input) {
        return new MonitorWithLinks(BaseMonitor.builder().metricName(input.getMetricName())
            .sensorClassName(input.getSensor().getClass().getCanonicalName())
            .interval(input.getInterval()).context(input.getMonitorContext()).build(),
            Links.selfLink("/monitors/" + input.getUuid()));
    }
}
