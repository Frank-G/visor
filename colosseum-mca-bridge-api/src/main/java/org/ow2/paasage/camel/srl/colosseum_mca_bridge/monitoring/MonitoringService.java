/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;


import java.util.Collection;
import java.util.Map;

/**
 * Created by daniel on 11.12.14.
 */
public interface MonitoringService {

    public void startMonitoring(String uuid, String metricName, String sensorClassName,
        Interval interval, Map<String, String> monitorContext)
        throws SensorNotFoundException, SensorInitializationException,
        InvalidMonitorContextException;

    public void stopMonitoring(String uuid);

    public Collection<Monitor> getMonitors();

    public Monitor getMonitor(String uuid);

    public boolean isMonitoring(String uuid);
}
