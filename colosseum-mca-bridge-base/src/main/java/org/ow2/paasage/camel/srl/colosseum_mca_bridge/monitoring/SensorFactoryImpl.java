/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 15.01.15.
 */
public class SensorFactoryImpl implements SensorFactory {

    @Override
    public Sensor from(String className) throws SensorNotFoundException, SensorInitializationException {
        checkNotNull(className);
        checkArgument(!className.isEmpty());
        return this.loadAndInitializeSensor(className);
    }

    protected Sensor loadAndInitializeSensor(String className) throws SensorNotFoundException, SensorInitializationException {
        try {
            Sensor sensor = (Sensor) Class.forName(className).newInstance();
            sensor.init();
            return sensor;
        } catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new SensorNotFoundException("Could not load sensor with name " + className, e);
        }
    }

}
