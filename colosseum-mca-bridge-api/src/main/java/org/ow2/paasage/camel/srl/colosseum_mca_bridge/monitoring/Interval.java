/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring;

import java.util.concurrent.TimeUnit;

/**
 * Created by daniel on 07.04.15.
 */
public interface Interval {
    long getPeriod();

    TimeUnit getTimeUnit();
}
