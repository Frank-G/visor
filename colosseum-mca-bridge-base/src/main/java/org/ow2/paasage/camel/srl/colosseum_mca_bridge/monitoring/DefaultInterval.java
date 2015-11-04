/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The interval class.
 * <p/>
 * Represents an interval consisting of a timeunit and a period.
 */
public class DefaultInterval implements Interval {

    /**
     * The period of the interval.
     */
    protected final long period;

    /**
     * The timeunit of the interval.
     */
    protected final TimeUnit timeUnit;

    /**
     * Constructor for the interval
     *
     * @param period   the period of the interval, must be larger then 0.
     * @param timeUnit the time unit of the interval.
     */
    public DefaultInterval(long period, TimeUnit timeUnit) {
        checkArgument(period > 0, "The period must be > 0");
        checkNotNull(timeUnit, "The time unit must not be null.");
        this.period = period;
        this.timeUnit = timeUnit;
    }

    public DefaultInterval(long period, String timeUnit) {
        this(period, TimeUnit.valueOf(timeUnit));
    }

    /**
     * Getter for the period.
     *
     * @return the period of the interval.
     */
    @Override public long getPeriod() {
        return period;
    }

    /**
     * Getter for the timeunit.
     *
     * @return the timeunit of the interval.
     */
    @Override public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override public String toString() {
        return String.format("Interval{period=%d, timeUnit=%s}", period, timeUnit);
    }
}
