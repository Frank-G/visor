/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.config;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Created by daniel on 17.12.14.
 */
public class ConfigurationModule extends AbstractModule {

    private final ConfigurationAccess configurationAccess;
    private final CommandLinePropertiesAccessor commandLinePropertiesAccessor;

    public ConfigurationModule(ConfigurationAccess configurationAccess,
        CommandLinePropertiesAccessor commandLinePropertiesAccessor) {
        this.commandLinePropertiesAccessor = commandLinePropertiesAccessor;
        this.configurationAccess = configurationAccess;
    }

    @Override protected void configure() {
        Names.bindProperties(binder(), configurationAccess.getProperties());
        Multibinder<IpProvider> ipProviderMultibinder =
            Multibinder.newSetBinder(binder(), IpProvider.class);
        ipProviderMultibinder.addBinding().toInstance(commandLinePropertiesAccessor);
        ipProviderMultibinder.addBinding().to(AwsIpWebService.class);
    }
}
