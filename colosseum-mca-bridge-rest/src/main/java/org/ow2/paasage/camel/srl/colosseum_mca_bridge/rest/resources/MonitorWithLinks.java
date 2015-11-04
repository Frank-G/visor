/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.resources;

import java.util.List;
import java.util.Set;

/**
 * Created by daniel on 07.04.15.
 */
public class MonitorWithLinks extends LinkWrapper<Monitor> implements MonitorEntity {

    public MonitorWithLinks(Monitor wrappedEntity, Set<Link> links) {
        super(wrappedEntity, links);

    }

    @Override public String getMetricName() {
        return this.wrappedEntity.getMetricName();
    }

    @Override public void setMetricName(String metricName) {
        this.wrappedEntity.setMetricName(metricName);
    }

    @Override public String getSensorClassName() {
        return this.wrappedEntity.getSensorClassName();
    }

    @Override public void setSensorClassName(String sensorClassName) {
        this.wrappedEntity.setSensorClassName(sensorClassName);
    }

    @Override public Interval getInterval() {
        return this.wrappedEntity.getInterval();
    }

    @Override public void setInterval(Interval interval) {
        this.wrappedEntity.setInterval(interval);
    }

    @Override public List<Context> getContexts() {
        return this.wrappedEntity.getContexts();
    }

    @Override public void setContexts(List<Context> contexts) {
        this.wrappedEntity.setContexts(contexts);
    }
}
