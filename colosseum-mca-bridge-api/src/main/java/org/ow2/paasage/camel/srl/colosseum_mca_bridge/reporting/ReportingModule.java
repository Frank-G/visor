/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;

/**
 * Created by daniel on 10.12.14.
 */
public abstract class ReportingModule extends AbstractModule {

    @Override protected void configure() {
        bind(new TypeLiteral<ReportingInterface<Metric>>() {
        }).annotatedWith(ExternalReporting.class).to(this.getReportingInterface());
    }

    protected abstract Class<? extends ReportingInterface<Metric>> getReportingInterface();

}
