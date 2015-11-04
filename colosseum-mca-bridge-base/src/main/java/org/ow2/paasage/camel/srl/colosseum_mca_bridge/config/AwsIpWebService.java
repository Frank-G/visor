/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.config;

import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by daniel on 10.02.15.
 */
@Singleton public class AwsIpWebService implements IpProvider {

    private static final String AWS_SERVICE = "http://checkip.amazonaws.com";
    private static final Logger LOGGER = LogManager.getLogger(AwsIpWebService.class);
    private String ipCache;

    private static String contactService() {
        URL whatIsMyIp;
        BufferedReader in = null;
        try {
            LOGGER.debug("Contacting AWS IP service at " + AWS_SERVICE);
            whatIsMyIp = new URL(AWS_SERVICE);
            in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream(),"UTF-8"));
            String ip = in.readLine();
            LOGGER.info("AWS IP service returned " + ip + " as public ip");
            return ip;
        } catch (IOException e) {
            LOGGER.error("Error contacting AWS IP service.", e);
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                    LOGGER.warn(ignored);
                }
            }
        }
    }

    @Nullable @Override public String getPublicIp() {
        if (this.ipCache == null) {
            this.ipCache = contactService();
        }
        return this.ipCache;
    }
}
