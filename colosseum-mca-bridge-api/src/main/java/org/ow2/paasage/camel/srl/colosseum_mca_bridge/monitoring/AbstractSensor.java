/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Daniel Baur
 */
public abstract class AbstractSensor implements Sensor {

    private MonitorContext monitorContext;
    private boolean isInitialized = false;


    @Override public void init() throws SensorInitializationException {
        this.initialize();
        this.isInitialized = true;
    }

    @Override public void setMonitorContext(MonitorContext monitorContext)
        throws InvalidMonitorContextException {
        Preconditions.checkNotNull(monitorContext);
        checkState(isInitialized);
        if (!validateMonitorContext(monitorContext)) {
            throw new InvalidMonitorContextException();
        }
        this.monitorContext = monitorContext;
    }

    @Override public Measurement getMeasurement() throws MeasurementNotAvailableException {
        checkState(isInitialized, "Measurement method was called before initialization.");
        checkNotNull(monitorContext != null,
            "Measurement method was called, before monitoring context was set.");
        return this.getMeasurement(this.monitorContext);
    }

    protected boolean validateMonitorContext(MonitorContext monitorContext) {
        return true;
    }

    protected void initialize() throws SensorInitializationException {
        // intentionally left empty
    }

    /**
     * Returns a single measurement object.
     *
     * @param monitorContext the context for the measurement.
     * @return a measurement taken by this sensor.
     * @throws MeasurementNotAvailableException
     */
    protected abstract Measurement getMeasurement(MonitorContext monitorContext)
        throws MeasurementNotAvailableException;

    @Override public String toString() {
        return this.getClass().getCanonicalName();
    }
}
