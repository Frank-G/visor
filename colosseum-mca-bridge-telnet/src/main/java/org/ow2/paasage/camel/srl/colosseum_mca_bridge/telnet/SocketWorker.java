/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.telnet;

import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingException;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by daniel on 15.12.14.
 */
public class SocketWorker implements Runnable {

    private final Socket socket;
    private final ReportingInterface<Metric> metricReporting;
    private final RequestParsingInterface<String, Metric> requestParser;
    private static final Logger LOGGER = LogManager.getLogger(SocketWorker.class);

    SocketWorker(final Socket socket, ReportingInterface<Metric> metricReporting,
        RequestParsingInterface<String, Metric> requestParser) {
        this.socket = socket;
        this.metricReporting = metricReporting;
        this.requestParser = requestParser;
    }

    protected void closeSocket() {
        try {
            this.socket.close();
        } catch (IOException ignored) {
            LOGGER.warn(ignored);
        }
    }

    @Override public void run() {
        LOGGER.debug("New connection to server");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                this.socket.setSoTimeout(20 * 1000);
                Scanner in = new Scanner(this.socket.getInputStream(), "UTF-8");
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    Metric metric = this.requestParser.parse(line);
                    LOGGER.debug("Server received new metric " + metric.getName());
                    this.metricReporting.report(metric);
                }
            } catch (IOException e) {
                LOGGER.error(e);
            } catch (ParsingException e) {
                LOGGER.error("Error parsing metric.", e);
            } catch (ReportingException e) {
                LOGGER.error("Could not report metric.", e);
            } finally {
                LOGGER.debug("Closing connection to server.");
                closeSocket();
                Thread.currentThread().interrupt();
            }
        }
    }
}
