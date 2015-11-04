/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.telnet;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.config.ConfigurationException;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.ExecutionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by daniel on 15.12.14.
 */
public class SocketServer {

    private static final Logger LOGGER = LogManager.getLogger(SocketServer.class);

    @Inject public SocketServer(@Named("telnetPort") int port, ExecutionService executionService,
        ServerListenerFactoryInterface serverListenerFactory) {
        checkArgument(port > 0, "Argument port must be > 0");
        if (port < 1024) {
            LOGGER.warn(
                "You are running the telnet server on a port < 1024. This is usually not a good idea.");
        }
        try {
            LOGGER.info(String.format("Starting socket server on port %d", port));
            ServerSocket serverSocket = new ServerSocket(port);
            executionService.execute(serverListenerFactory.create(serverSocket));
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }
}
