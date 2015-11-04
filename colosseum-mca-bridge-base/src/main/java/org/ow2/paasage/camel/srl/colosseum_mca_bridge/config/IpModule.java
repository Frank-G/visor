/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.util.Set;

/**
 * Created by daniel on 08.04.15.
 */
public class IpModule extends AbstractModule {

    @Override protected void configure() {

    }

    @Provides @Named("localIp") public String provideIp(Set<IpProvider> ipProviders) {
        for (IpProvider ipProvider : ipProviders) {
            if (ipProvider.getPublicIp() != null) {
                return ipProvider.getPublicIp();
            }
        }
        throw new ConfigurationException("Could not resolve the ip address.");
    }
}
