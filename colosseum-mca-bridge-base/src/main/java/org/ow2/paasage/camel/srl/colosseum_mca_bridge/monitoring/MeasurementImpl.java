/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;


/**
 * Created by daniel on 18.12.14.
 */
public class MeasurementImpl implements Measurement {

    private final long timestamp;
    private final Object value;

    public MeasurementImpl(long timestamp, Object value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
