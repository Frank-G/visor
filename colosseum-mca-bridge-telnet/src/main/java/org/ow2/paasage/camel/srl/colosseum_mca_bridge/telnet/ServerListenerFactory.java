/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.telnet;

import com.google.inject.Inject;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.ExecutionService;

import java.net.ServerSocket;

/**
 * Created by daniel on 16.12.14.
 */
public class ServerListenerFactory implements ServerListenerFactoryInterface {

    private final SocketWorkerFactory socketWorkerFactory;
    private final ExecutionService executionService;

    @Inject
    public ServerListenerFactory(ExecutionService executionService, SocketWorkerFactory socketWorkerFactory) {
        this.executionService = executionService;
        this.socketWorkerFactory = socketWorkerFactory;
    }

    @Override
    public ServerListener create(ServerSocket serverSocket) {
        return new ServerListener(serverSocket, this.executionService, this.socketWorkerFactory);
    }
}
