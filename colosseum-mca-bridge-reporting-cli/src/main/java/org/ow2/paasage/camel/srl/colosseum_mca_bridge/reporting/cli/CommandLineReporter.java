/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.cli;


import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingException;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingInterface;

import java.util.Collection;

/**
 * Created by daniel on 27.11.14.
 */
public class CommandLineReporter implements ReportingInterface<Metric> {

    @Override public void report(Metric metric) throws ReportingException {
        System.out.println(metric.toString());
    }

    @Override public void report(Collection<Metric> metrics) throws ReportingException {
        for (Metric metric : metrics) {
            this.report(metric);
        }
    }
}
