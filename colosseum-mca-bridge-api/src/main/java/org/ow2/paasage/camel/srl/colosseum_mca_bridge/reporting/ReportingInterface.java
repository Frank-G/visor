/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting;

import java.util.Collection;

/**
 * Interface for reporting generic items.
 *
 * @param <T> the class of the reported item.
 * @todo: split in single and multiple...
 */
public interface ReportingInterface<T> {

    /**
     * Reports the generic item.
     *
     * @param item the item to report.
     * @throws ReportingException If an error occurred while reporting the item.
     */
    public void report(T item) throws ReportingException;

    /**
     * Reports a collection of the generic item.
     *
     * @param items a collection of generic items to report.
     * @throws ReportingException If an error occurred while reporting on of the items.
     */
    public void report(Collection<T> items) throws ReportingException;
}
