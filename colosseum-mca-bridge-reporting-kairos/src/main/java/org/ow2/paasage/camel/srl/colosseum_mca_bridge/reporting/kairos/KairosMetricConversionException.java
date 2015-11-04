/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.kairos;

/**
 * This exception is thrown if a metric could not be converted.
 */
public class KairosMetricConversionException extends Exception {

    /**
     * @see java.lang.Exception
     */
    public KairosMetricConversionException() {
    }

    /**
     * @see java.lang.Exception
     */
    public KairosMetricConversionException(String message) {
        super(message);
    }

    /**
     * @see java.lang.Exception
     */
    public KairosMetricConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see java.lang.Exception
     */
    public KairosMetricConversionException(Throwable cause) {
        super(cause);
    }

    /**
     * @see java.lang.Exception
     */
    public KairosMetricConversionException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
