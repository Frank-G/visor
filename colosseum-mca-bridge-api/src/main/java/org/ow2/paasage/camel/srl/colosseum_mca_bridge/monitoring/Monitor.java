/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;



import org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution.Schedulable;

/**
 * Created by daniel on 18.12.14.
 */
public interface Monitor extends Schedulable {

    public String getUuid();

    public String getMetricName();

    public Sensor getSensor();

    public MonitorContext getMonitorContext();
}
