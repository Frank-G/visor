/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.ScheduledExecutionService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 11.12.14.
 */
@Singleton public class MonitoringServiceImpl implements MonitoringService {

    private final Map<String, Monitor> monitorRegistry;
    private final ScheduledExecutionService scheduler;
    private final SensorFactory sensorFactory;
    private final MonitorFactory monitorFactory;

    @Inject
    public MonitoringServiceImpl(ScheduledExecutionService scheduler, SensorFactory sensorFactory,
        MonitorFactory monitorFactory) {
        this.scheduler = scheduler;
        this.sensorFactory = sensorFactory;
        this.monitorFactory = monitorFactory;
        monitorRegistry = new HashMap<>();
    }

    @Override public void startMonitoring(String uuid, String metricName, String sensorClassName,
        Interval interval, Map<String, String> monitorContext)
        throws SensorNotFoundException, SensorInitializationException,
        InvalidMonitorContextException {

        checkNotNull(uuid);
        checkArgument(!uuid.isEmpty());

        checkNotNull(uuid);
        checkArgument(!uuid.isEmpty());

        checkNotNull(metricName);
        checkArgument(!metricName.isEmpty());

        checkNotNull(sensorClassName);
        checkArgument(!sensorClassName.isEmpty());

        checkNotNull(interval);

        checkNotNull(monitorContext);

        final Sensor sensor = this.sensorFactory.from(sensorClassName);
        final Monitor monitor =
            this.monitorFactory.create(uuid, metricName, sensor, interval, monitorContext);
        this.monitorRegistry.put(uuid, monitor);
        this.scheduler.schedule(monitor);
    }

    @Override public void stopMonitoring(String uuid) {
        checkArgument(isMonitoring(uuid));
        this.scheduler.remove(this.monitorRegistry.get(uuid));
        this.monitorRegistry.remove(uuid);
    }

    @Override public Collection<Monitor> getMonitors() {
        return this.monitorRegistry.values();
    }

    @Override public Monitor getMonitor(String uuid) {
        return this.monitorRegistry.get(uuid);
    }

    @Override public boolean isMonitoring(String uuid) {
        return this.monitorRegistry.containsKey(uuid);
    }
}
