/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.resources;

/**
 * Created by daniel on 09.02.15.
 */
public class Interval {

    private String timeUnit;

    private long period;

    Interval() {
    }

    Interval(long period, String timeUnit) {
        this.period = period;
        this.timeUnit = timeUnit;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getPeriod() {
        return period;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setPeriod(long period) {
        this.period = period;
    }
}
