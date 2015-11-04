/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.telnet;

import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.ExecutionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by daniel on 15.12.14.
 */
public class ServerListener implements Runnable {

    private final ServerSocket serverSocket;
    private final ExecutionService executionService;
    private final SocketWorkerFactoryInterface socketWorkerFactory;
    private static final Logger LOGGER = LogManager.getLogger(SocketServer.class);

    ServerListener(ServerSocket serverSocket, ExecutionService executionService,
        SocketWorkerFactoryInterface socketWorkerFactory) {
        this.serverSocket = serverSocket;
        this.executionService = executionService;
        this.socketWorkerFactory = socketWorkerFactory;
    }

    @Override public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket accept = this.serverSocket.accept();
                this.executionService.execute(this.socketWorkerFactory.create(accept));
            } catch (IOException e) {
                LOGGER.error("Error occurred while accepting connection.", e);
            }
        }
        try {
            this.serverSocket.close();
        } catch (IOException ignored) {
            LOGGER.warn(ignored);
        }
    }
}
