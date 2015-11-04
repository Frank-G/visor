/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by daniel on 15.12.14.
 */
public class ShutdownHook extends Thread {

    private static final Logger logger = LogManager.getLogger(ShutdownHook.class);

    private final ExecutionService executionService;
    private final int reportingInterval;

    @Inject public ShutdownHook(ExecutionService executionService,
        @Named("reportingInterval") int reportingInterval) {
        this.executionService = executionService;
        this.reportingInterval = reportingInterval;
    }

    @Override public void run() {
        logger.debug("Running shutdown hook. Using reporting Interval*2 (" + reportingInterval * 2
            + ") as timeout for the execution.");
        this.executionService.shutdown(reportingInterval * 2);
    }
}
