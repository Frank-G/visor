/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.QueuedReporting;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingInterface;

import java.util.Map;

/**
 * Created by daniel on 15.01.15.
 */
public class MonitorFactoryImpl implements MonitorFactory {

    private final String localIp;
    private final ReportingInterface<Metric> metricReportingInterface;

    @Inject public MonitorFactoryImpl(@Named("localIp") String localIp,
        @QueuedReporting ReportingInterface<Metric> metricReportingInterface) {
        this.localIp = localIp;
        this.metricReportingInterface = metricReportingInterface;
    }

    @Override
    public Monitor create(String uuid, String metricName, Sensor sensor, Interval interval,
        Map<String, String> context) throws InvalidMonitorContextException {
        MonitorContext monitorContext =
            DefaultMonitorContext.builder().addContext(DefaultMonitorContext.LOCAL_IP, localIp)
                .addContext(context).build();
        return new MonitorImpl(uuid, metricName, sensor, interval, monitorContext,
            metricReportingInterface);
    }
}
