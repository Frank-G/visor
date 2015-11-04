/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.cli;


import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingInterface;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingModule;

/**
 * Created by daniel on 15.12.14.
 */
public class CommandLineReportingModule extends ReportingModule {
    @Override protected Class<? extends ReportingInterface<Metric>> getReportingInterface() {
        return CommandLineReporter.class;
    }
}
