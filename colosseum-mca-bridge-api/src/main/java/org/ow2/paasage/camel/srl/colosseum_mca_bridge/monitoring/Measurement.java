/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

/**
 * Represents a measurement taken by a sensor.
 *
 * @author Daniel Baur
 */
public interface Measurement {

    /**
     * The timestamp the measurement object was taken.
     *
     * @return unix timestamp
     */
    public long getTimestamp();

    /**
     * The value for this measurement at the defined timestamp.
     *
     * @return a object representing the value. manadatory
     */
    public Object getValue();
}
