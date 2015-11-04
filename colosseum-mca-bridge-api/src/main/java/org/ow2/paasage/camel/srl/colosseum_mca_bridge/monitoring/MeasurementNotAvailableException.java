/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

/**
 * This exception is thrown if a probe could not execute its measurement, and
 * the metric is therefore not available.
 */
public class MeasurementNotAvailableException extends Exception {

    /**
     * @see java.lang.Exception
     */
    public MeasurementNotAvailableException(Throwable cause) {
        super(cause);
    }

    /**
     * @see java.lang.Exception
     */
    public MeasurementNotAvailableException(String message) {
        super(message);
    }

    /**
     * @see java.lang.Exception
     */
    public MeasurementNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
