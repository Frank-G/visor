/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

public interface Sensor {

    /**
     * Initializes the sensor.
     * <p>
     * This e.g. allows the sensor to install dependencies for the operating
     * system, or allows the sensor to configure itself for the current
     * environment.
     *
     * @throws SensorInitializationException for problems during the initialization
     */
    public void init() throws SensorInitializationException;

    /**
     * Sets the monitor context for the sensor.
     * <p>
     * Tells the sensor the context in which it is running.
     *
     * @param monitorContext context of the sensor.
     *
     * @throws InvalidMonitorContextException if the monitor context is not valid.
     */
    public void setMonitorContext(MonitorContext monitorContext) throws InvalidMonitorContextException;

    /**
     * Called to retrieve a measurement from this probe.
     *
     * @return the current measurement for this probe.
     *
     * @throws MeasurementNotAvailableException
     */
    public Measurement getMeasurement() throws MeasurementNotAvailableException;
}
