/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.config;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.DefaultScheduledExecutionService;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.ExecutionService;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.ScheduledExecutionService;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.*;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 08.04.15.
 */
public class BaseModule extends AbstractModule {

    private final ConfigurationAccess configurationAccess;
    private final CommandLinePropertiesAccessor commandLinePropertiesAccessor;

    public BaseModule(ConfigurationAccess configurationAccess,
        CommandLinePropertiesAccessor commandLinePropertiesAccessor) {
        checkNotNull(configurationAccess);
        checkNotNull(commandLinePropertiesAccessor);
        this.commandLinePropertiesAccessor = commandLinePropertiesAccessor;
        this.configurationAccess = configurationAccess;
    }

    @Override protected void configure() {
        install(new ConfigurationModule(configurationAccess, commandLinePropertiesAccessor));
        install(new IpModule());
        bind(ExecutionService.class).to(DefaultScheduledExecutionService.class);
        bind(new TypeLiteral<ReportingInterface<Metric>>() {
        }).annotatedWith(QueuedReporting.class).to(new TypeLiteral<Queue<Metric>>() {
        });
        bind(new TypeLiteral<QueueWorkerFactoryInterface<Metric>>() {
        }).to(new TypeLiteral<QueueWorkerFactory<Metric>>() {
        });
        bind(MonitoringService.class).to(MonitoringServiceImpl.class);
        bind(ScheduledExecutionService.class).to(DefaultScheduledExecutionService.class);
        bind(MonitorFactory.class).to(MonitorFactoryImpl.class);
        bind(SensorFactory.class).to(SensorFactoryImpl.class);
    }



}
