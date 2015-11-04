/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.config;

import com.google.inject.Singleton;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by daniel on 15.12.14.
 */
@Singleton public class FileConfigurationAccessor implements ConfigurationAccess {

    private final Properties properties;

    public FileConfigurationAccessor(String configurationFilePath) {
        this.properties = new Properties();
        try (final FileInputStream fileInputStream = new FileInputStream(configurationFilePath);
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(
                fileInputStream)) {
            properties.load(bufferedInputStream);
        } catch (IOException e) {
            throw new ConfigurationException("Could not read properties file.", e);
        }
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    @Override public Properties getProperties() {
        return this.properties;
    }
}
