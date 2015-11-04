/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingException;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by daniel on 18.12.14.
 */
public class MonitorImpl implements Monitor {

    private static final Logger LOGGER = LogManager.getLogger(Monitor.class);
    private final String uuid;
    private final String metricName;
    private final Sensor sensor;
    private final MonitorContext monitorContext;
    private final MonitorWorker monitorWorker;
    private final Interval interval;

    public MonitorImpl(String uuid, String metricName, Sensor sensor, Interval interval,
        MonitorContext monitorContext, ReportingInterface<Metric> metricReportingInterface)
        throws InvalidMonitorContextException {
        this.uuid = uuid;
        this.metricName = metricName;
        this.sensor = sensor;
        this.monitorContext = monitorContext;
        this.sensor.setMonitorContext(monitorContext);
        this.monitorWorker = new MonitorWorker(this, metricReportingInterface);
        this.interval = interval;
    }

    @Override public String getUuid() {
        return this.uuid;
    }

    @Override public String getMetricName() {
        return metricName;
    }

    @Override public Sensor getSensor() {
        return sensor;
    }

    @Override public MonitorContext getMonitorContext() {
        return monitorContext;
    }

    @Override public Interval getInterval() {
        return this.interval;
    }

    @Override public Runnable getRunnable() {
        return this.monitorWorker;
    }

    @Override public String toString() {
        return "MonitorImpl{" +
            "uuid='" + uuid + '\'' +
            ", metricName='" + metricName + '\'' +
            ", sensor=" + sensor +
            ", monitorContext=" + monitorContext +
            ", monitorWorker=" + monitorWorker +
            ", interval=" + interval +
            '}';
    }

    private static class MonitorWorker implements Runnable {

        private final Monitor monitor;
        private final ReportingInterface<Metric> metricReportingInterface;

        public MonitorWorker(Monitor monitor, ReportingInterface<Metric> metricReportingInterface) {
            this.monitor = monitor;
            this.metricReportingInterface = metricReportingInterface;
        }

        @Override public void run() {
            try {
                LOGGER.debug("Measuring Monitor " + this.monitor);
                this.metricReportingInterface.report(MetricFactory
                    .from(monitor.getMetricName(), monitor.getSensor().getMeasurement(),
                        monitor.getMonitorContext()));
            } catch (MeasurementNotAvailableException e) {
                LOGGER.error("Could not retrieve metric", e);
            } catch (ReportingException e) {
                LOGGER.error("Could not report metric", e);
            }
        }
    }

}
