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
 * The MemoryUsageProbe class.
 * <p>
 * Measures the current
 * ly used memory by the operating system in percentage.
 */
public class MemoryUsageSensor extends AbstractSensor {

    private OperatingSystemMXBean osBean;

    @Override protected Measurement getMeasurement(MonitorContext monitorContext)
        throws MeasurementNotAvailableException {
        //memory usage
        double totalPhysicalMemory = osBean.getTotalPhysicalMemorySize();
        double freePhysicalMemory = osBean.getFreePhysicalMemorySize();

        if (totalPhysicalMemory < 0 || freePhysicalMemory < 0) {
            throw new MeasurementNotAvailableException(
                "Received negative value for total or free physical memory size");
        }

        return new MeasurementImpl(System.currentTimeMillis(),
            100 - ((freePhysicalMemory / totalPhysicalMemory) * 100));
    }

    @Override protected void initialize() throws SensorInitializationException {
        super.initialize();
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }
}
