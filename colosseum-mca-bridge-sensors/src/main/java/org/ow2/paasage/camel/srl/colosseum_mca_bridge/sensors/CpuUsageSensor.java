/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.sensors;

import com.sun.management.OperatingSystemMXBean;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.*;

import java.lang.management.ManagementFactory;

/**
 * A probe for measuring the CPU usage in % on the given machine.
 */
public class CpuUsageSensor extends AbstractSensor {

    private OperatingSystemMXBean osBean;

    @Override protected Measurement getMeasurement(MonitorContext monitorContext)
        throws MeasurementNotAvailableException {

        double systemCpuLoad = osBean.getSystemCpuLoad();
        double systemCpuLoadPercentage = systemCpuLoad * 100;

        if (systemCpuLoad < 0) {
            throw new MeasurementNotAvailableException("Received negative value");
        }

        return new MeasurementImpl(System.currentTimeMillis(), systemCpuLoadPercentage);
    }

    @Override protected void initialize() throws SensorInitializationException {
        super.initialize();
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }
}
