/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.ShutdownHook;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.RestServer;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.telnet.SocketServer;

import java.util.Set;

/**
 * Created by daniel on 17.12.14.
 */
public class VisorService {

    private final Set<Module> modules;

    public VisorService(Set<Module> modules) {
        this.modules = modules;
    }

    public void start() {
        final Injector injector = Guice.createInjector(this.modules);

        injector.getInstance(SocketServer.class);
        injector.getInstance(RestServer.class);
        Runtime.getRuntime().addShutdownHook(injector.getInstance(ShutdownHook.class));
    }
}
