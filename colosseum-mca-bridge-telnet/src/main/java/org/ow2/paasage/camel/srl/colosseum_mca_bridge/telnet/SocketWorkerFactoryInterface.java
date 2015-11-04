/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.telnet;

import com.google.inject.ImplementedBy;

import java.net.Socket;

/**
 * Created by daniel on 16.12.14.
 */
@ImplementedBy(SocketWorkerFactory.class)
public interface SocketWorkerFactoryInterface {

    public SocketWorker create(final Socket socket);

}
