/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting;

import com.google.inject.Inject;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Interval;

import java.util.concurrent.BlockingQueue;

/**
 * Created by daniel on 12.12.14.
 */
public class QueueWorkerFactory<T> implements QueueWorkerFactoryInterface<T> {

    private final ReportingInterface<T> reportingInterface;

    @Inject public QueueWorkerFactory(@ExternalReporting ReportingInterface<T> reportingInterface) {
        this.reportingInterface = reportingInterface;
    }

    @Override public QueueWorker<T> create(BlockingQueue<T> queue, Interval interval) {
        return new QueueWorker<>(queue, this.reportingInterface, interval);
    }
}
