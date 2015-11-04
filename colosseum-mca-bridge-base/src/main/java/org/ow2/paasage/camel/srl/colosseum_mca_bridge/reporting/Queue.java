/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.ScheduledExecutionService;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.DefaultInterval;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A generic implementation of the reporting interface using a
 * queue as "buffer" for the reporting.
 * All items store in the queue will be later reported to the concrete reporting interface.
 *
 * @param <T> the class of the generic item.
 */
@Singleton public class Queue<T> implements ReportingInterface<T> {

    /**
     * The queue storing the items.
     */
    private final BlockingQueue<T> queueDelegate;
    private static final Logger LOGGER = LogManager.getLogger(Queue.class);

    @Inject public Queue(ScheduledExecutionService executionService,
        QueueWorkerFactoryInterface<T> queueWorkerFactory,
        @Named("reportingInterval") int reportingInterval) {
        this.queueDelegate = new LinkedBlockingQueue<>();
        executionService.schedule(queueWorkerFactory
            .create(this.queueDelegate, new DefaultInterval(reportingInterval, TimeUnit.SECONDS)));
    }

    @Override public void report(T item) throws ReportingException {
        if (this.queueDelegate.remainingCapacity() == 0) {
            throw new ReportingException("Item could not be reported as queue is full.");
        }
        try {
            this.queueDelegate.put(item);
        } catch (InterruptedException e) {
            LOGGER.error(e);
        }
    }

    @Override public void report(Collection<T> items) throws ReportingException {
        for (T item : items) {
            this.report(item);
        }
    }
}
