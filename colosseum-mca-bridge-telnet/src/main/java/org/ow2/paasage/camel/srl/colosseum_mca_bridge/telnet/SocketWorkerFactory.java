/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.telnet;

import com.google.inject.Inject;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.QueuedReporting;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingInterface;

import java.net.Socket;

/**
 * Created by daniel on 16.12.14.
 */
public class SocketWorkerFactory implements SocketWorkerFactoryInterface {


    private final ReportingInterface<Metric> metricReporting;
    private final RequestParsingInterface<String, Metric> requestParser;

    @Inject public SocketWorkerFactory(@QueuedReporting ReportingInterface<Metric> metricReporting,
        RequestParsingInterface<String, Metric> requestParser) {
        this.metricReporting = metricReporting;
        this.requestParser = requestParser;
    }

    @Override public SocketWorker create(Socket socket) {
        return new SocketWorker(socket, metricReporting, requestParser);
    }
}
