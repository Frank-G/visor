/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.config;

import com.google.inject.Inject;
import org.apache.commons.cli.*;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by daniel on 24.09.14.
 */
@SuppressWarnings("AccessStaticViaInstance") public class CommandLinePropertiesAccessorImpl
    implements CommandLinePropertiesAccessor {

    private final Options options;
    private CommandLine commandLine;
    private final static BasicParser parser = new BasicParser();
    private final static HelpFormatter helpFormatter = new HelpFormatter();

    @Inject @Singleton public CommandLinePropertiesAccessorImpl(String[] args) {
        this.options = new Options();
        this.generateOptions(this.options);

        try {
            this.commandLine = this.parser.parse(options, args);
        } catch (ParseException e) {
            this.commandLine = null;
            throw new ConfigurationException(e);
        }
    }

    private void generateOptions(Options options) {
        options.addOption(
            OptionBuilder.withLongOpt("localIp").withDescription("IP of the local machine").hasArg()
                .create("ip"));
        options.addOption(
            OptionBuilder.withLongOpt("configFile").withDescription("Configuration file location.")
                .isRequired().hasArg().create("conf"));
    }

    public void printHelp() {
        helpFormatter.printHelp("java -jar [args] colosseum-mca-bridge.jar", options);
    }

    @Nullable protected String getCommandLineOption(String name) {
        checkState(this.commandLine != null, "Command line not parsed.");
        if (!commandLine.hasOption(name)) {
            return null;
        }
        return commandLine.getOptionValue(name);
    }

    @Override public String getConfFileLocation() {
        String confFile = this.getCommandLineOption("conf");
        checkState(confFile != null, "No command line argument value for conf (configFile)");
        return confFile;
    }

    @Override @Nullable public String getPublicIp() {
        return getCommandLineOption("ip");
    }
}
