/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge;

import com.google.inject.Module;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.config.*;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.telnet.ServerModule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 17.12.14.
 */
public class VisorServiceBuilder {

    private String[] args;
    private Set<Module> modules;

    private VisorServiceBuilder() {
        this.modules = new HashSet<>();
    }

    public static VisorServiceBuilder create() {
        return new VisorServiceBuilder();
    }

    public VisorServiceBuilder args(String[] args) {
        checkNotNull(args);
        this.args = args;
        return this;
    }

    public VisorServiceBuilder modules(Module... modules) {
        checkNotNull(modules);
        this.modules.addAll(Arrays.asList(modules));
        return this;
    }

    private void loadModulesBasedOnConfiguration(ConfigurationAccess configurationAccess) {
        try {
            this.modules.add((Module) Class
                .forName(configurationAccess.getProperties().getProperty("reportingModule"))
                .newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new ConfigurationException(e);
        }
    }

    public VisorService build() {
        //create the config file access
        CommandLinePropertiesAccessor commandLinePropertiesAccessor =
            new CommandLinePropertiesAccessorImpl(this.args);
        ConfigurationAccess configurationAccess =
            new FileConfigurationAccessor(commandLinePropertiesAccessor.getConfFileLocation());
        this.modules.add(new BaseModule(configurationAccess, commandLinePropertiesAccessor));
        this.modules.add(new ServerModule());
        this.loadModulesBasedOnConfiguration(configurationAccess);
        return new VisorService(this.modules);
    }


}
