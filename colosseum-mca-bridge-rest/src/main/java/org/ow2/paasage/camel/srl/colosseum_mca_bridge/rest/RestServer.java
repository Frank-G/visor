/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.config.ConfigurationException;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.ExecutionService;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.MonitoringService;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.controllers.MonitorController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 06.02.15.
 */
public class RestServer {

    private static final Logger LOGGER = LogManager.getLogger(RestServer.class);

    @Inject public RestServer(@Named("restPort") int restPort, @Named("restHost") String restHost,
        MonitoringService monitoringService, ExecutionService executionService) {
        checkArgument(restPort > 0);

        if (restPort <= 1024) {
            LOGGER.warn("You try to open a port below 1024. This is usual not a good idea...");
        }
        checkNotNull(restHost);
        checkArgument(!restHost.isEmpty());

        URI baseUri = UriBuilder.fromUri(restHost).port(restPort).build();
        ResourceConfig config = new ResourceConfig();
        config.register(new MonitorController(monitoringService));
        config.register(JacksonFeature.class);
        executionService.execute(new GrizzlyServer(baseUri, config));
    }

    public static class GrizzlyServer implements Runnable {

        private final URI baseUri;
        private final ResourceConfig config;

        private GrizzlyServer(URI baseUri, ResourceConfig config) {
            this.baseUri = baseUri;
            this.config = config;
        }

        @Override public void run() {
            try {
                GrizzlyHttpServerFactory.createHttpServer(baseUri, config).start();
            } catch (IOException e) {
                throw new ConfigurationException(e);
            }
        }
    }
}
