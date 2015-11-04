/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting;

import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.Schedulable;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Interval;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Generic implementation of a queue worker.
 *
 * @param <T>
 */
public class QueueWorker<T> implements Schedulable, Runnable {

    private final BlockingQueue<T> queue;
    private final ReportingInterface<T> reportingInterface;
    private static final Logger LOGGER = LogManager.getLogger(QueueWorker.class);
    private final Interval interval;

    public QueueWorker(BlockingQueue<T> queue, ReportingInterface<T> reportingInterface,
        Interval interval) {
        this.queue = queue;
        this.reportingInterface = reportingInterface;
        this.interval = interval;
    }

    @Override public void run() {
        List<T> tList = new ArrayList<>();
        this.queue.drainTo(tList);
        try {
            LOGGER.info("Reporting " + tList.size() + " items.");
            this.reportingInterface.report(tList);
        } catch (ReportingException e) {
            LOGGER.error("Could not report metrics, throwing them away.", e);
        }
    }

    @Override public Interval getInterval() {
        return this.interval;
    }

    @Override public Runnable getRunnable() {
        return this;
    }
}
